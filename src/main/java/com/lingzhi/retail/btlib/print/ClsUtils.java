package com.lingzhi.retail.btlib.print;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *  蓝牙操作--反射
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public class ClsUtils {

	/**
	 * 与设备配对 参考源码：platform/packages/apps/Settings.git
	 * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
	 */
	public static boolean createBond(Class btClass, BluetoothDevice btDevice)
			throws Exception {
		Method createBondMethod = btClass.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * 与设备解除配对 参考源码：platform/packages/apps/Settings.git
	 * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
	 */
	public static boolean removeBond(Class btClass, BluetoothDevice btDevice)
			throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	/**
	 * 设置pin
	 * @param btClass 蓝牙设备class
	 * @param btDevice 蓝牙设备
	 * @param str pin码
	 * @return
	 * @throws Exception
	 */
	public static boolean setPin(Class btClass, BluetoothDevice btDevice,
	                             String str) throws Exception {
		try {
			Method setPinMethod = btClass.getDeclaredMethod("setPin",
					new Class[]
							{byte[].class});
			Boolean returnValue = (Boolean) setPinMethod.invoke(btDevice,
					new Object[]
							{str.getBytes()});
			Log.e("returnValue", "" + returnValue);
			return returnValue.booleanValue();
		}
		catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	/**
	 *  取消用户输入
	 * @param btClass BluetoothDevice class
	 * @param device BluetoothDevice
	 * @return
	 * @throws Exception
	 */
	public static boolean cancelPairingUserInput(Class btClass,
	                                             BluetoothDevice device) throws Exception {
		Method cancelPairingUserInputMethod = btClass.getMethod("cancelPairingUserInput");
		// cancelBondProcess()
		Boolean returnValue = (Boolean) cancelPairingUserInputMethod.invoke(device);
		return returnValue.booleanValue();
	}

	/**
	 *  取消配对
	 * @param btClass BluetoothDevice class
	 * @param device BluetoothDevice
	 * @return
	 * @throws Exception
	 */
	static public boolean cancelBondProcess(Class btClass,
	                                        BluetoothDevice device) throws Exception {
		Method cancelBondProcessMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) cancelBondProcessMethod.invoke(device);
		return returnValue.booleanValue();
	}

	/**
	 * 打印所有方法
	 * @param clsShow
	 */
	public static void printAllInform(Class clsShow) {
		try {
			// 取得所有方法
			Method[] hideMethod = clsShow.getMethods();
			int i = 0;
			for (; i < hideMethod.length; i++) {
				Log.e("method name", hideMethod[i].getName() + ";and the i is:"
						+ i);
			}
			// 取得所有常量
			Field[] allFields = clsShow.getFields();
			for (i = 0; i < allFields.length; i++) {
				Log.e("Field name", allFields[i].getName());
			}
		}
		catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
