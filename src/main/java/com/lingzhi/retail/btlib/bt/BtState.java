package com.lingzhi.retail.btlib.bt;

/**
 *  蓝牙状态
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public enum BtState {

    /** 蓝牙不支持*/
    BT_STATE_UNSUPPORT,
    /** 蓝牙未打开 */
    BT_STATE_UNOPEN,
    /** 蓝牙已经打开 */
    BT_STATE_OPEN,
    /** 未绑定 */
    BT_STATE_UNBIND,
    /** 正在绑定（手动），只有auto模式才会触发 */
    BT_STATE_BINDING,
    /** 蓝牙已经绑定 */
    BT_STATE_BINDED,

}
