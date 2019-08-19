package com.lingzhi.retail.btlib.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import java.util.Set;

import com.lingzhi.retail.btlib.bt.BtUtil;

/**
 *  打印通用工具类
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public class PrintUtil {

    private static final String FILENAME = "thbt";
    private static final String DEFAULT_BLUETOOTH_DEVICE_ADDRESS = "default_bluetooth_device_address";
    private static final String DEFAULT_BLUETOOTH_DEVICE_NAME = "default_bluetooth_device_name";

    public static final String ACTION_PRINT_ORDER = "print_order";
    public static final String ACTION_PRINT_DATA = "print_data";


    /**
     *  设置默认连接蓝牙的mac地址
     * @param mContext 上下文
     * @param value 地址
     */
    public static void setDefaultBluetoothDeviceAddress(Context mContext, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_BLUETOOTH_DEVICE_ADDRESS, value);
        editor.apply();
    }

    /**
     *  设置默认蓝牙设备名称
     * @param mContext 上下文
     * @param value 设备名称
     */
    public static void setDefaultBluetoothDeviceName(Context mContext, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_BLUETOOTH_DEVICE_NAME, value);
        editor.apply();
    }

    /**
     *  是否绑定
     * @param mContext 上下文
     * @param bluetoothAdapter 蓝牙适配器
     * @return
     */
    public static boolean isBondPrinter(Context mContext, BluetoothAdapter bluetoothAdapter) {
        if (!BtUtil.isOpen(bluetoothAdapter)) {
            return false;
        }
        String defaultBluetoothDeviceAddress = getDefaultBluethoothDeviceAddress(mContext);
        if (TextUtils.isEmpty(defaultBluetoothDeviceAddress)) {
            return false;
        }
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        if (deviceSet == null || deviceSet.isEmpty()) {
            return false;
        }
        for (BluetoothDevice bluetoothDevice : deviceSet) {
            if (bluetoothDevice.getAddress().equals(defaultBluetoothDeviceAddress)) {
                return true;
            }
        }
        return false;

    }

    /**
     *  获取默认蓝牙mac地址
     * @param mContext 上下文
     * @return mac地址
     */
    public static String getDefaultBluethoothDeviceAddress(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEFAULT_BLUETOOTH_DEVICE_ADDRESS, "");
    }

    /**
     *  是否有默认绑定的打印机
     * @param mContext
     * @return
     */
    public static boolean isBondPrinterIgnoreBluetooth(Context mContext) {
        String defaultBluetoothDeviceAddress = getDefaultBluethoothDeviceAddress(mContext);
        return !(TextUtils.isEmpty(defaultBluetoothDeviceAddress)
                || TextUtils.isEmpty(getDefaultBluetoothDeviceName(mContext)));
    }

    public static String getDefaultBluetoothDeviceName(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEFAULT_BLUETOOTH_DEVICE_NAME, "");
    }

    /**
     * use new api to reduce file operate
     *
     * @param editor editor
     */
    public static void apply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

}
