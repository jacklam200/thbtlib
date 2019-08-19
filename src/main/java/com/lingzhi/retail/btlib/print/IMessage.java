package com.lingzhi.retail.btlib.print;

import android.content.Context;

import java.util.List;

/**
 *  打印消息
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public interface IMessage {
    List<byte[]> getData(Context context);
}
