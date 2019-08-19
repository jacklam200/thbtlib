package com.lingzhi.retail.btlib.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *  打印队列
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public class PrintQueue {

    /**
     *  打印监听
     */
    public static interface IPrintListener{
        /**
         *  打印连接
         * @param result 结果
         * @param code 代码
         * @param data 只有在读写时的数据，其他一概为null(包括用数组数据写的)
         */
        void onPrintConnectMessage(int  result, int code, byte[] data);

    }

    private boolean mIsConnecting = false;
    /**
     * print queue
     */
    private ArrayList<byte[]> mQueue;
    /** 蓝牙打印 */
    private BtPrintClient mBtService;
    /** 蓝牙结果回调 */
    private IPrintListener mListener;

    /**
     * bluetooth adapter
     */
    private BluetoothAdapter mAdapter;

    /**
     *  单例，因为蓝牙存在每个生命
     * @author linzaixiong
     * @version 1.0
     * @updated  2019/8/1
     */
    static class QueueInstance{
        static final PrintQueue INSTANCE = new PrintQueue();
    }

    /**
     *  获取打印队列
     * @return
     */
    public static PrintQueue getQueue() {
        return QueueInstance.INSTANCE;
    }

    /**
     * 增加打印，并打印 ，建议使用带数组的，因为打印机缓存不是很大
     * @param context 上下文
     * @param bytes 数据
     * @param listener 回调
     */
    public synchronized void add(Context context, byte[] bytes, IPrintListener listener) {

        if (null == mQueue) {
            mQueue = new ArrayList<byte[]>();
        }
        if (null != bytes) {
            mQueue.add(bytes);
        }

        if(mListener != listener) {
            mListener = listener;
        }

        print(context);
    }

    /**
     *  设置打印回调
     * @param listener
     */
    public void setListener(IPrintListener listener){
        mListener = listener;
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

        if(mListener != listener) {
            mListener = listener;
        }

        print(context);
    }


    /**
     *  打印
     * @param context 上下文
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
            boolean isDone = false;
            while (mQueue.size() > 0) {
                Log.d("PrintQueue", "recycle queue");
                if (mBtService.getState() == BtPrintClient.STATE_CONNECTED) {
                    mBtService.write(mQueue.get(0));
                    isDone = true;
                }
                mQueue.remove(0);
            }

            if(isDone){
                mHandler.obtainMessage( Constants.MESSAGE_WRITE_ALL).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  获取连接的设备
     * @return
     */
    public String getDeviceAddress(){

        String address = "";

        boolean hasDevice = mBtService != null &&
                (mBtService.getState() == BtPrintClient.STATE_CONNECTED || mBtService.getState() == BtPrintClient.STATE_CONNECTING);
        if(hasDevice){
            address = mBtService.getDevice();
        }

        return address;
    }

    /**
     * 获取连接设备名称
     * @return Name
     */
    public String getDeviceName(){

        String name = "";
        boolean hasDevice = mBtService != null &&
                (mBtService.getState() == BtPrintClient.STATE_CONNECTED || mBtService.getState() == BtPrintClient.STATE_CONNECTING);
        if(hasDevice){
            name = mBtService.getDeviceName();
        }

        return name;
    }

    /**
     *  是否已连接
     * @return
     */
    public boolean isConnect(){

        boolean isConnect = false;
        boolean hasDevice = mBtService != null &&
                (mBtService.getState() == BtPrintClient.STATE_CONNECTED || mBtService.getState() == BtPrintClient.STATE_CONNECTING);
        if(hasDevice){
            isConnect = true;
        }

        return isConnect;
    }

    /**
     *  尝试连接
     * @param btAddress
     * @param listener
     * @return
     */
    public synchronized boolean tryConnect(String btAddress, IPrintListener listener) {

        boolean isConnect = false;

        if(!mIsConnecting){

            mListener = listener;
            try {
                if (null == mAdapter) {
                    mAdapter = BluetoothAdapter.getDefaultAdapter();
                }
                if (TextUtils.isEmpty(btAddress) || null == mAdapter) {

                }
                else{
                    if (null == mBtService) {
                        mBtService = new BtPrintClient(mHandler);
                    }

                    if (mBtService.getState() != BtPrintClient.STATE_CONNECTED) {
                        if (!TextUtils.isEmpty(btAddress)) {
                            mIsConnecting = true;
                            BluetoothDevice device = mAdapter.getRemoteDevice(btAddress);
                            mBtService.connect(device, false);
                            isConnect = true;
                        }
                    } else {
                        isConnect = true;
                    }
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
     * 断开连接
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

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    if(mListener != null) {
                        // BtPrintClient.STATE_CONNECTED
                        //  BtPrintClient.STATE_CONNECTING
                        // BtPrintClient.STATE_LISTEN
                        // BtPrintClient.STATE_NONE 凡是失败，或者重置都会发出NONE
                        if(msg.arg1 == BtPrintClient.STATE_CONNECTED ||
                                msg.arg1 == BtPrintClient.STATE_NONE){
                            mIsConnecting = false;
                        }
                        mListener.onPrintConnectMessage(msg.arg1, Constants.MESSAGE_STATE_CHANGE, null);
                    }
                    break;

                case Constants.MESSAGE_READ:
                    mIsConnecting = false;
                    if(mListener != null) {
                        mListener.onPrintConnectMessage(Constants.MESSAGE_SUCCESS, Constants.MESSAGE_READ, (byte[]) msg.obj);
                    }
                    break;

                case Constants.MESSAGE_WRITE:
                    mIsConnecting = false;
                    if(mListener != null) {
                        mListener.onPrintConnectMessage(Constants.MESSAGE_SUCCESS, Constants.MESSAGE_WRITE, (byte[])msg.obj);
                    }
                    break;

                case Constants.MESSAGE_WRITE_ALL:
                    mIsConnecting = false;
                    if(mListener != null) {
                        mListener.onPrintConnectMessage(Constants.MESSAGE_SUCCESS, Constants.MESSAGE_WRITE_ALL, null);
                    }
                    break;

                case Constants.MESSAGE_WRITE_FAILED:
                    // 打印失败
                    Log.d("PrintQueue", "Constants.MESSAGE_WRITE_FAILED");
                    mIsConnecting = false;
                    if(mListener != null) {
                        mListener.onPrintConnectMessage(Constants.MESSAGE_FAIL, Constants.MESSAGE_WRITE_FAILED, (byte[])msg.obj);
                    }
                    break;

                case Constants.MESSAGE_TOAST:
                    //  1.不能连接设备 2.连接丢失
                    // Constants.MESSAGE_CONNECT_FAILED Constants.MESSAGE_CONNECT_LOST
                    mIsConnecting = false;
                    if(mListener != null) {
                        mListener.onPrintConnectMessage(Constants.MESSAGE_FAIL,  msg.arg1, null);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
