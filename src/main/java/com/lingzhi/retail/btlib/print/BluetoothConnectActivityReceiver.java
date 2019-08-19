package com.lingzhi.retail.btlib.print;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *  蓝牙连接广播
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public class BluetoothConnectActivityReceiver extends BroadcastReceiver {

	String strPsw = "0000";
	private static final String ACTION_RECEIVER = "android.bluetooth.device.action.PAIRING_REQUEST";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_RECEIVER)) {
			BluetoothDevice btDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			try {
				// 手机和蓝牙采集器配对
				ClsUtils.setPin(btDevice.getClass(), btDevice, strPsw);
				ClsUtils.createBond(btDevice.getClass(), btDevice);
				ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
