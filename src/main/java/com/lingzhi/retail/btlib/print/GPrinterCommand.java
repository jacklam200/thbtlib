package com.lingzhi.retail.btlib.print;

/**
 *  蓝牙通用指令
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public class GPrinterCommand {

    /**
     *  靠左
     */
    public static final byte[] LEFT = new byte[]{0x1b, 0x61, 0x00};
    /**
     * 居中
     */
    public static final byte[] CENTER = new byte[]{0x1b, 0x61, 0x01};
    /**
     * 靠右
     */
    public static final byte[] RIGHT = new byte[]{0x1b, 0x61, 0x02};
    /**
     * 选择加粗模式
     */
    public static final byte[] BOLD = new byte[]{0x1b, 0x45, 0x01};
    /**
     * 取消加粗模式
     */
    public static final byte[] BOLD_CANCEL = new byte[]{0x1b, 0x45, 0x00};
    /**
     * 字体不放大
     */
    public static final byte[] TEXT_NORMAL_SIZE = new byte[]{0x1d, 0x21, 0x00};
    /**
     * 高加倍
     */
    public static final byte[] TEXT_BIG_HEIGHT = new byte[]{0x1b, 0x21, 0x10};
    /**
     * 宽高加倍
     */
    public static final byte[] TEXT_BIG_SIZE = new byte[]{0x1d, 0x21, 0x11};
    /**
     * 复位打印机
     */
    public static final byte[] RESET = new byte[]{0x1b, 0x40};
    /**
     * 打印并换行
     */
    public static final byte[] PRINT = new byte[]{0x0a};
    /**
     * 下划线
     */
    public static final byte[] UNDER_LINE = new byte[]{0x1b, 0x2d, 2};
    /**
     * 下划线
     */
    public static final byte[] UNDER_LINE_CANCEL = new byte[]{0x1b, 0x2d, 0};

    /**
     * 走纸
     *
     * @param n 行数
     * @return 命令
     */
    public static byte[] walkPaper(byte n) {
        return new byte[]{0x1b, 0x64, n};
    }

    /**
     * 设置横向和纵向移动单位
     *
     * @param x 横向移动
     * @param y 纵向移动
     * @return 命令
     */
    public static byte[] move(byte x, byte y) {
        return new byte[]{0x1d, 0x50, x, y};
    }

    /**
     *  默认空间
     * @return
     */
    public static byte [] defaultLineSpace(){
        return  new byte[]{0x1b,0x32};
    }

    /***
     * 设置横向和纵向距离
     * @param x
     * @param y
     * @return
     */
    public static byte [] gspXY(byte x,byte y){
        return new byte[]{0x1D,0x50,x,y};
    }
}
