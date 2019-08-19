package com.lingzhi.retail.btlib.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 *  蓝牙打印客户端
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public class BtPrintClient {

    private static final String TAG = "BtPrintClient";

    /** Name for the SDP record when creating server socket */
    private static final String NAME_SECURE = "BBtPrintClientSecure";
    private static final String NAME_INSECURE = "BBtPrintClientInsecure";

    /**  每个应用的 Unique UUID */
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /**  UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"); */
    private static final UUID MY_UUID_INSECURE =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    /**  Member fields */
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    private String deviceAddr ="";
    private String deviceName="";

    /**  Constants that indicate the current connection state */
    /**  默认状态 */
    public static final int STATE_NONE = 0;
    /**  监听状态 */
    public static final int STATE_LISTEN = 1;
     /**  正在连接状态 */
    public static final int STATE_CONNECTING = 2;
     /**  已连接状态  */
    public static final int STATE_CONNECTED = 3;

    /**
     *  构造函数
     *
     * @param context The UI Activity Context
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BtPrintClient(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * 构造函数
     *
     * @param context The UI Activity Context
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BtPrintClient( Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * 更改状态
     *
     * @param state STATE_NONE， STATE_LISTEN， STATE_CONNECTING， STATE_CONNECTED
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * 返回当前连接状态
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * 开启服务
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // 取消掉任何一个准备连接的
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // 取消掉任何一个正连接着的
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // 设置为监听状态
        setState(STATE_LISTEN);

        // 开启一个listen 分安全和不安全的
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);
        deviceAddr = "";
        deviceName ="";
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        setState(STATE_CONNECTING);
        mConnectThread.start();

    }

    /**
     * 开始ConnectedThread，并管理蓝牙连接
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        setState(STATE_CONNECTED);
        mConnectedThread.start();
        deviceName = device.getName();
        deviceAddr = device.getAddress();
        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);


    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");
        deviceAddr = "";
        deviceName="";
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * 通过connectedthread 输入数据
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     *  获取设置
     * @return
     */
    public String getDevice() {

        String address = "";

        if (mState == STATE_CONNECTED){
            address = deviceAddr;
        }

        return address;
    }

    /**
     *  获取设备名字
     * @return
     */
    public String getDeviceName() {

        String address = "";

        if (mState == STATE_CONNECTED){
            address = deviceName;
        }

        return address;
    }

    /**
     *  连接失败
     */
    private void connectionFailed() {
        setState(BtPrintClient.STATE_NONE);
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        msg.arg1 = Constants.MESSAGE_CONNECT_FAILED;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
//        this.start();
    }

    /**
     *连接丢失
     */
    private void connectionLost() {
        setState(BtPrintClient.STATE_NONE);
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        msg.arg1 = Constants.MESSAGE_CONNECT_LOST;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
//        this.start();
    }

    /**
     *
     *  接收连接， 被接受连接后会进入ConnectThread
     *
     */
    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {

            BluetoothServerSocket tmp = null;

            mSocketType = secure ? "Secure" : "Insecure";

            //创建一个监听的服务
            try {
                if (secure) {
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                            MY_UUID_SECURE);
                } else {
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }

            mmServerSocket = tmp;
        }

        @Override
        public void run() {

            Log.d(TAG, "Socket Type: " + mSocketType +
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    if(mmServerSocket != null) {
                        socket = mmServerSocket.accept();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }

                // 如果连接被接受
                if (socket != null) {

                    synchronized (BtPrintClient.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }


    /**
     *
     *
     *  触发连接操作，并开启connectedThread进入 connected
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // 连接成功取消发现服务
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                //  socket连接
                mmSocket.connect();
                Log.i(TAG, "connect:" + mSocketType);

            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                Log.i(TAG, "connectionFailed:" + mSocketType);
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BtPrintClient.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        /**
         *  取消
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    /**
     *
     * 连接后，处理设备的接收和发送数据
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    Log.e(TAG, "read");
                    bytes = mmInStream.read(buffer);
                    Log.e(TAG, "read success");
                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
//                    BtPrintClient.this.start();
                    break;
                }catch (Exception e){
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                }
            }
        }

        /**
         * 往蓝牙写数据
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                if(mState == STATE_CONNECTED){
                    mmOutStream.write(buffer);

                    // Share the sent message back to the UI Activity
                    mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                            .sendToTarget();
                    Log.e(TAG, "write MESSAGE_WRITE");
                }
                else{
                    setState(STATE_NONE);
                    // 失败会告知状态
                    mHandler.obtainMessage(Constants.MESSAGE_WRITE_FAILED, -1, -1, buffer)
                            .sendToTarget();
                }


            } catch (IOException e) {
                // 失败会告知状态
                setState(STATE_NONE);
                mHandler.obtainMessage(Constants.MESSAGE_WRITE_FAILED, -1, -1, buffer)
                        .sendToTarget();
                Log.e(TAG, "write MESSAGE_WRITE_FAILED");
                Log.e(TAG, "Exception during write", e);
            }
        }

        /**
         *  关闭socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
