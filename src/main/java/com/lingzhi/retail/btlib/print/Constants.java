package com.lingzhi.retail.btlib.print;

/**
 *  常量
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public class Constants {

    public static final int MESSAGE_SUCCESS = 0;
    public static final int MESSAGE_FAIL = -1;
    // BtPrintClients 与handler 交互

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE_FAILED = 6;

    public static final int MESSAGE_WRITE_ALL = 7;

    /**
     *  连接失败
     */
    public static final int MESSAGE_CONNECT_FAILED = 0x01;
    /**
     *  连接丢失
     */
    public static final int MESSAGE_CONNECT_LOST = 0x02;


    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
}
