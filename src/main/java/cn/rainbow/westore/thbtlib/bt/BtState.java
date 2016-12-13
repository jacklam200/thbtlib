package cn.rainbow.westore.thbtlib.bt;

/**
 * Created by LinZaixiong on 2016/8/29.
 */
public enum BtState {

    BT_STATE_UNSUPPORT, /** 蓝牙不支持*/
    BT_STATE_UNOPEN, /** 蓝牙未打开 */
    BT_STATE_OPEN, /** 蓝牙已经打开 */
    BT_STATE_UNBIND,  /** 未绑定 */
    BT_STATE_BINDING, /** 正在绑定（手动），只有auto模式才会触发 */
    BT_STATE_BINDED, /** 蓝牙已经绑定 */

}
