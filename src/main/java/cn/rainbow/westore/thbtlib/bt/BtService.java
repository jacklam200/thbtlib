package cn.rainbow.westore.thbtlib.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.Set;

import cn.rainbow.westore.thbtlib.print.PrintUtil;

/**
 *
 * Created by LinZaixiong on 2016/8/29.
 */
public class BtService {

    private BluetoothAdapter mBluetoothAdapter;
    private IBtListener mListener;
    private Context mContext;
    private int mRequestCode = -1;
    private BtState mCurrentState = BtState.BT_STATE_UNSUPPORT;
    private BtState mLastState = BtState.BT_STATE_UNSUPPORT;
    private boolean mIsAuto;

    public BtService(Context context, IBtListener listener, int requestCode){
        mContext = context;
        mListener = listener;
        mRequestCode = requestCode;
        mIsAuto = true;
    }

    public BtService(Context context, IBtListener listener){
        mContext = context;
        mListener = listener;
    }

    public BtService(){

    }

    public void start(Context context){

        mContext = context;

        if(context != null){

            mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null){ // unsupport bt device

                unsupportBt(mBluetoothAdapter);
            }
            else{ // support bt device

                supportBt(mBluetoothAdapter);
            }
        }
    }

    public void reset(){
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

    public void setBtListener(IBtListener listener){
        mListener = listener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(mIsAuto && requestCode == mRequestCode && mRequestCode > 0){

            if( resultCode == Activity.RESULT_OK){
                callBack(BtState.BT_STATE_OPEN, "", "");
                start(mContext);
            }
            else{
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
             String address = PrintUtil.getDefaultBluethoothDeviceAddress(mContext.getApplicationContext());
             if (TextUtils.isEmpty(address)) {
                 unBind(adapter);
             }
             else{
                 String name = PrintUtil.getDefaultBluetoothDeviceName(mContext.getApplicationContext());
                 Bind(name, address);
             }

         }
        else{
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
                if (null != deviceSet) {

//                    callBack(BtState.BT_STATE_BINDING, "", "");
                    if(mListener != null){
                        mListener.onBtDevice(deviceSet);
                    }
                }
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




}
