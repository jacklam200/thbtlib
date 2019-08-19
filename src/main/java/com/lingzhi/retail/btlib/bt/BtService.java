package com.lingzhi.retail.btlib.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.lingzhi.retail.btlib.print.PrintUtil;

/**
 *  蓝牙服务，提供打开，绑定回调，并发现设备
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public class BtService {
    public static final int VERSION_1 = 0x01;
    public static final int VERSION_2 = 0x02;
    private BluetoothAdapter mBluetoothAdapter;
    private IBtListener mListener;
    private Context mContext;
    private int mRequestCode = -1;
    private BtState mCurrentState = BtState.BT_STATE_UNSUPPORT;
    private BtState mLastState = BtState.BT_STATE_UNSUPPORT;
    private boolean mIsAuto;
    private boolean isRegister = false;
    private int mVersion = VERSION_1;

    public BtService(Context context, IBtListener listener, int requestCode){
       this(context, listener, requestCode, VERSION_1);
    }

    public BtService(Context context, IBtListener listener, int requestCode, int version){
        mContext = context;
        mListener = listener;
        mRequestCode = requestCode;
        mVersion = version;
        mIsAuto = true;
    }

    public BtService(Context context, IBtListener listener){
        mContext = context;
        mListener = listener;
    }

    public BtService(){

    }

    /**
     *  开始打开蓝牙，并检测蓝牙状态，有默认，默认回调绑定
     * @param context
     * @return
     */
    public boolean start(Context context){

        boolean isStart = false;
        mContext = context;

        if(context != null){

            mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
            // 1.whether support bt
            if(mBluetoothAdapter == null){
                // 不支持蓝牙
                unsupportBt(mBluetoothAdapter);
            }
            else{
                // support bt device
                isStart = true;
                supportBt(mBluetoothAdapter);
            }
        }

        return isStart;
    }

    /**
     *  取消发现，通知置位
     */
    public void reset(){
	    if(mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(mContext);
        mCurrentState = BtState.BT_STATE_UNSUPPORT;
        mLastState = BtState.BT_STATE_UNSUPPORT;
    }

    /**
     *  根据地址获取设备
     * @param address 地址
     * @return
     */
    public BluetoothDevice getDevice(String address){
        BluetoothDevice device = null;
        if(mBluetoothAdapter != null){
            device = mBluetoothAdapter.getRemoteDevice(address);
        }

        return device;
    }

    /**
     *  注册蓝牙接收广播
     * @param context 上下文
     */
    public void registerReceiver(Context context){
        // 注册广播接收器。接收蓝牙发现讯息
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);
        isRegister = true;

    }

    /**
     *  反注册蓝牙接收广播
     * @param context 上下文
     */
    public void unregisterReceiver(Context context){
        if(isRegister){
            if(mReceiver != null){
                context.unregisterReceiver(mReceiver);
            }
            isRegister = false;
        }

    }


    /**
     *  设置蓝牙监听器
     * @param listener
     */
    public void setBtListener(IBtListener listener){
        mListener = listener;
    }

    /**
     *  回调结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(mIsAuto && requestCode == mRequestCode && mRequestCode > 0){

            if( resultCode == Activity.RESULT_OK){
                // 2.1 bt is opened
                callBack(BtState.BT_STATE_OPEN, "", "");
                start(mContext);
            }
            else{
                // 2.2 bt is always closed
                callBack(BtState.BT_STATE_UNOPEN, "", "");
            }


        }
    }

    /**
     *  回调不支持蓝牙
     * @param adapter
     */
    private void unsupportBt(BluetoothAdapter adapter){

        callBack(BtState.BT_STATE_UNSUPPORT, "", "");
    }

    /**
     *  支持蓝牙的，进行蓝牙开启操作，绑定操作，之前有存储，默认会去绑定
     * @param adapter
     */
    private void supportBt(BluetoothAdapter adapter){

         if(adapter != null && adapter.isEnabled()){


             open();
             callbackData(adapter);
             // 3. whether has default bt device to connect
             String address = PrintUtil.getDefaultBluethoothDeviceAddress(mContext.getApplicationContext());
             if (TextUtils.isEmpty(address)) {
                 // didn't bind any bt device
                 unBind(adapter);
             }
             else{
                 String name = PrintUtil.getDefaultBluetoothDeviceName(mContext.getApplicationContext());
                 // has bind a bt device
                 Bind(name, address);
             }

         }
        else{
             // 2. bt disable, open bt
             unOpen();

         }
    }

    /**
     *  未打开
     */
    private void unOpen(){

        callBack(BtState.BT_STATE_UNOPEN, "", "");

        if(mIsAuto && mRequestCode > 0 && mContext instanceof Activity){
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)mContext).startActivityForResult(enableBtIntent, mRequestCode);
        }
    }

    /**
     *  打开
     */
    private void open(){
        callBack(BtState.BT_STATE_OPEN, "", "");
    }

    /**
     *  未绑定
     * @param adapter
     */
    private void unBind(BluetoothAdapter adapter){
        callBack(BtState.BT_STATE_UNBIND, "", "");


    }

    /**
     *  数据回调
     * @param adapter
     */
    private void callbackData(BluetoothAdapter adapter){
        if(mIsAuto){

            if (null != adapter) {

                Set<BluetoothDevice> deviceSet = adapter.getBondedDevices();
                if (null != deviceSet && deviceSet.size() > 0) {

//                    callBack(BtState.BT_STATE_BINDING, "", "");
                    if(mListener != null){
                        mListener.onBtDevice(deviceSet, true);
                    }
                }
                // 注册广播
                registerReceiver(mContext);
                // 开始发现
                adapter.startDiscovery();

            }
        }
    }

    /**
     *  绑定蓝牙
     * @param name 蓝牙名字
     * @param address 地址
     */
    private void Bind(String name, String address){

        callBack(BtState.BT_STATE_BINDED, name, address);
    }


    /**
     *  回调
     * @param  currentState 当前状态
     * @param name 设备名称
     * @param address 地址
     * @return 是否回调成功
     */
    private boolean callBack(BtState currentState, String name, String address){

        mCurrentState = currentState;

        if(mLastState == mCurrentState){
            return false;
        }


        if(mListener != null){
            mListener.onBtMessage(currentState, name, address);
        }

        mLastState = mCurrentState;

        return true;
    }

    /**
     *  蓝牙广播
     * @author linzaixiong
     * @version 1.0
     * @updated  2019/8/1
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // 找到一个设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Set<BluetoothDevice> deviceSet = new HashSet<BluetoothDevice>(Arrays.asList(device));
                deviceSet = Collections.unmodifiableSet(deviceSet);
                if(mListener != null){
                    // 回调设备，且告知未配对
                    mListener.onBtDevice(deviceSet, false);
                }
            }
        }
    };


}
