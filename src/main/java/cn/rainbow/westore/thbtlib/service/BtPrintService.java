package cn.rainbow.westore.thbtlib.service;

import android.app.IntentService;
import android.content.Intent;

import cn.rainbow.westore.thbtlib.print.PrintUtil;

/**
 * Created by LinZaixiong on 2016/8/30.
 */
public class BtPrintService extends IntentService {

    public BtPrintService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        if (intent.getAction().equals(PrintUtil.ACTION_PRINT_ORDER)) {
            print(intent.getStringExtra(PrintUtil.ACTION_PRINT_DATA));
        }


    }

    private void print(String data){

    }

}
