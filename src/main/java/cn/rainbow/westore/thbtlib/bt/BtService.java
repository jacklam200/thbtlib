package cn.rainbow.westore.thbtlib.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.rainbow.westore.thbtlib.print.PrintUtil;

/**
 *
 * Created by LinZaixiong on 2016/8/29.
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

    public boolean start(Context context){

        boolean isStart = false;
        mContext = context;

        if(context != null){

            mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
            // 1.whether support bt
            if(mBluetoothAdapter == null){ // unsupport bt device

                unsupportBt(mBluetoothAdapter);
            }
            else{ // support bt device
                isStart = true;
                supportBt(mBluetoothAdapter);
            }
        }

        return isStart;
    }

    public void reset(){
	    if(mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(mContext);
        mCurrentState = BtState.BT_STATE_UNSUPPORT;
        mLastState = BtState.BT_STATE_UNSUPPORT;
    }

    public BluetoothDevice getDevice(String address){
        BluetoothDevice device = null;
        if(mBluetoothAdapter != null){
            device = mBluetoothAdapter.getRemoteDevice(address);
        }

        return device;
    }

    public void registerReceiver(Context context){
        // 注册广播接收器。接收蓝牙发现讯息
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);
        isRegister = true;

    }

    public void unregisterReceiver(Context context){
        if(isRegister){
            context.unregisterReceiver(mReceiver);
            isRegister = false;
        }

    }


    public void setBtListener(IBtListener listener){
        mListener = listener;
    }

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

    private void unsupportBt(BluetoothAdapter adapter){

        callBack(BtState.BT_STATE_UNSUPPORT, "", "");
    }

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

    private void unOpen(){

        callBack(BtState.BT_STATE_UNOPEN, "", "");

        if(mIsAuto && mRequestCode > 0 && mContext instanceof Activity){
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)mContext).startActivityForResult(enableBtIntent, mRequestCode);
        }
    }

    private void open(){
        callBack(BtState.BT_STATE_OPEN, "", "");
    }

    private void unBind(BluetoothAdapter adapter){
        callBack(BtState.BT_STATE_UNBIND, "", "");


    }

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

                registerReceiver(mContext);
                adapter.startDiscovery();

            }
        }
    }

    private void Bind(String name, String address){

        callBack(BtState.BT_STATE_BINDED, name, address);
    }


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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Set<BluetoothDevice> deviceSet = new HashSet<BluetoothDevice>(Arrays.asList(device));
                deviceSet = Collections.unmodifiableSet(deviceSet);
                if(mListener != null){
                    mListener.onBtDevice(deviceSet, false);
                }
            }
        }
    };


}
