package cn.rainbow.westore.thbtlib.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LinZaixiong on 2016/8/30.
 */
public class PrintQueue {

    public static interface IPrintListener{
        void onPrintConnectMessage(int connectResult, int result);
    }

    private boolean mIsConnecting = false;
    /**
     * print queue
     */
    private ArrayList<byte[]> mQueue;

    private BtPrintClient mBtService;

    private IPrintListener mListener;

    /**
     * bluetooth adapter
     */
    private BluetoothAdapter mAdapter;

    static class QueueInstance{
        static PrintQueue mInstatnce = new PrintQueue();
    }

    public static PrintQueue getQueue() {
        return QueueInstance.mInstatnce;
    }

    public synchronized void add(Context context, byte[] bytes, IPrintListener listener) {

        if (null == mQueue) {
            mQueue = new ArrayList<byte[]>();
        }
        if (null != bytes) {
            mQueue.add(bytes);
        }

        if(mListener != listener)
            mListener = listener;

        print(context);
    }

    /**
     * add print bytes to queue. and call print
     *
     * @param bytesList bytesList
     */
    public synchronized void add(Context context, List<byte[]> bytesList, IPrintListener listener) {
        if (null == mQueue) {
            mQueue = new ArrayList<byte[]>();
        }
        if (null != bytesList) {
            mQueue.addAll(bytesList);
        }

        if(mListener != listener)
            mListener = listener;

        print(context);
    }


    /**
     * print queue
     */
    public synchronized void print(Context context) {
        try {
            if (null == mQueue || mQueue.size() <= 0) {
                return;
            }
            if (null == mAdapter) {
                mAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (null == mBtService) {
                mBtService = new BtPrintClient(mHandler);
            }

            if (mBtService.getState() != BtPrintClient.STATE_CONNECTED) {

                if (context != null && !TextUtils.isEmpty(PrintUtil.getDefaultBluethoothDeviceAddress(context))) {
                    BluetoothDevice device = mAdapter.getRemoteDevice(PrintUtil.getDefaultBluethoothDeviceAddress(context));
                    mBtService.connect(device, false);
                    return;
                }
            }
            while (mQueue.size() > 0) {
                Log.d("PrintQueue", "recycle queue");
                if (mBtService.getState() == BtPrintClient.STATE_CONNECTED)
                    mBtService.write(mQueue.get(0));
                mQueue.remove(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDeviceAddress(){

        String addr = "";

        if(mBtService != null &&
                (mBtService.getState() == BtPrintClient.STATE_CONNECTED || mBtService.getState() == BtPrintClient.STATE_CONNECTING)){
            addr = mBtService.getDevice();
        }

        return addr;
    }

    public boolean isConnect(){

        boolean isConnect = false;

        if(mBtService != null &&
                (mBtService.getState() == BtPrintClient.STATE_CONNECTED || mBtService.getState() == BtPrintClient.STATE_CONNECTING)){
            isConnect = true;
        }

        return isConnect;
    }

    /**
     * when bluetooth status is changed, if the printer is in use,
     * connect it,else do nothing
     */
    public synchronized boolean tryConnect(String btAddress, IPrintListener listener) {

        boolean isConnect = false;

        if(!mIsConnecting){
            mListener = listener;
            try {
                if (TextUtils.isEmpty(btAddress)) {
                    return isConnect;
                }
                if (null == mAdapter) {
                    mAdapter = BluetoothAdapter.getDefaultAdapter();
                }
                if (null == mAdapter) {
                    return isConnect;
                }

                if (null == mBtService) {
                    mBtService = new BtPrintClient(mHandler);
                }
                if (mBtService.getState() != BtPrintClient.STATE_CONNECTED) {
                    if (!TextUtils.isEmpty(btAddress)) {
                        mIsConnecting = true;
                        BluetoothDevice device = mAdapter.getRemoteDevice(btAddress);
                        mBtService.connect(device, false);
                        isConnect = true;
                        return isConnect;
                    }
                } else {
                    isConnect = true;
                }
            } catch (Exception e) {
                mIsConnecting = false;
                e.printStackTrace();
            } catch (Error e) {
                mIsConnecting = false;
                e.printStackTrace();
            }
        }


        return isConnect;

    }

    /**
     * disconnect remote device
     */
    public void disconnect() {
        try {
            if (null != mBtService) {
                mBtService.stop();
                mBtService = null;
            }
            if (null != mAdapter) {
                mAdapter = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BtPrintClient.STATE_CONNECTED:
                            mIsConnecting = false;
//                            Toast.makeText(SearchDeviceActivity.this,"连接成功！",Toast.LENGTH_SHORT).show();
//                            mAdater.printerStateChange(mChatService.getAddrese());
//                            print(null);

                            break;
                        case BtPrintClient.STATE_CONNECTING:
                            break;
                        case BtPrintClient.STATE_LISTEN:
                        case BtPrintClient.STATE_NONE:
                            mIsConnecting = false;
                            break;
                    }
                    if(mListener != null)
                        mListener.onPrintConnectMessage(msg.arg1,  Constants.MESSAGE_STATE_CHANGE);
                    break;
                case Constants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    Toast.makeText(SearchDeviceActivity.this,readMessage,Toast.LENGTH_SHORT).show();
                    mIsConnecting = false;
                    if(mListener != null)
                        mListener.onPrintConnectMessage(-1,  Constants.MESSAGE_READ);
                    break;
                case Constants.MESSAGE_WRITE:
                    mIsConnecting = false;
                    if(mListener != null)
                        mListener.onPrintConnectMessage(-1,  Constants.MESSAGE_WRITE);
                    break;
                case Constants.MESSAGE_WRITE_FAILED:
                    Log.d("PrintQueue", "Constants.MESSAGE_WRITE_FAILED");
                    mIsConnecting = false;
                    if(mListener != null)
                        mListener.onPrintConnectMessage(-1,  Constants.MESSAGE_WRITE_FAILED);
                    break;

                case Constants.MESSAGE_TOAST:
//                    Toast.makeText(SearchDeviceActivity.this, msg.getData().getString(Constants.TOAST),
//                            Toast.LENGTH_SHORT).show();
                    mIsConnecting = false;
                    if(mListener != null)
                        mListener.onPrintConnectMessage(-1,  Constants.MESSAGE_TOAST);
                    break;
            }
        }
    };
}
