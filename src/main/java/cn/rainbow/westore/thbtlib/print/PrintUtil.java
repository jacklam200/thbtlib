package cn.rainbow.westore.thbtlib.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import java.util.Set;

import cn.rainbow.westore.thbtlib.bt.BtUtil;

/**
 * Created by LinZaixiong on 2016/8/29.
 */
public class PrintUtil {

    private static final String FILENAME = "thbt";
    private static final String DEFAULT_BLUETOOTH_DEVICE_ADDRESS = "default_bluetooth_device_address";
    private static final String DEFAULT_BLUETOOTH_DEVICE_NAME = "default_bluetooth_device_name";

    public static final String ACTION_PRINT_ORDER = "print_order";
    public static final String ACTION_PRINT_DATA = "print_data";


    public static void setDefaultBluetoothDeviceAddress(Context mContext, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_BLUETOOTH_DEVICE_ADDRESS, value);
        editor.apply();
//        AppInfo.btAddress = value;
    }

    public static void setDefaultBluetoothDeviceName(Context mContext, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_BLUETOOTH_DEVICE_NAME, value);
        editor.apply();
//        AppInfo.btName = value;
    }

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

    public static String getDefaultBluethoothDeviceAddress(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEFAULT_BLUETOOTH_DEVICE_ADDRESS, "");
    }

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
