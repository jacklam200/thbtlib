package com.lingzhi.retail.btlib.bt;

import android.bluetooth.BluetoothDevice;

import com.lingzhi.retail.btlib.bt.BtState;

import java.util.Set;

/**
 *  蓝牙回调
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public interface IBtListener {

    /**
     *  蓝牙消息
     * @param state @see{@link com.lingzhi.retail.btlib.bt.BtState}
     * @param name
     * @param address
     */
    void onBtMessage(BtState state, String name, String address);


    /**
     *  回调蓝牙设备
     * @param deviceSet 发现的设备
     * @param isPair 是否配对
     */
    void onBtDevice(Set<BluetoothDevice> deviceSet, boolean isPair);
}
