package cn.rainbow.westore.thbtlib.bt;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

import cn.rainbow.westore.thbtlib.bt.BtState;

/**
 * Created by LinZaixiong on 2016/8/29.
 */
public interface IBtListener {
    void onBtMessage(BtState state, String name, String address);

    /**
     *  只有在unbind的时候才会回调
     * @param deviceSet
     */
    void onBtDevice(Set<BluetoothDevice> deviceSet);
}
