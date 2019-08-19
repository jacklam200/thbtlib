package com.lingzhi.retail.btlib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 *  note your comments
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/7/24
 */
public class DemoActivity extends Activity implements  View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Button mBtCallself;
    private Button mBtCalljs;
    private SeekBar mPbProgress;
    private SeekBar mSbColor;
    private SeekBar mSbTransform;
    private SeekBar mSbAlpha;
    private Integer mColor = Color.BLUE & 0x77FFFFFF;
    private Integer mTransform = Color.BLUE & 0x77FFFFFF;
    private float mAlpha = 0.3f;
    private boolean isHasTitle;

    private TextView mTvColor;
    private TextView mTvTransform;
    private TextView mTvAlpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mPbProgress = (SeekBar) findViewById(R.id.pb_progress);
        mBtCallself = (Button) findViewById(R.id.bt_callself);
        mBtCalljs = (Button) findViewById(R.id.bt_calljs);
        mBtCalljs.setOnClickListener(this);
        mBtCallself.setOnClickListener(this);




        mTvColor = (TextView) findViewById(R.id.tv_color);
        mTvTransform = (TextView) findViewById(R.id.tv_transform);
        mTvAlpha = (TextView) findViewById(R.id.tv_alpha);

        mTvColor.setText(""+Integer.toHexString(mColor));
        mTvTransform.setText(""+Integer.toHexString(mTransform));
        mTvAlpha.setText(""+mAlpha);

        mSbColor = (SeekBar) findViewById(R.id.sb_color);
        mSbTransform = (SeekBar) findViewById(R.id.sb_transform);
        mSbAlpha = (SeekBar) findViewById(R.id.sb_alpha);

        mSbColor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mColor = progress;
                mColor |= 0xff000000;
	            mTvColor.setText(""+Integer.toHexString(mColor));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSbTransform.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTransform = progress;
                mTransform  |= 0xff000000;
	            mTvTransform.setText(""+Integer.toHexString(mTransform));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSbAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAlpha = (float)progress / (float)100;
                mTvAlpha.setText(""+mAlpha);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void initData() {

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.bt_callself){


        }
        else if(v.getId() == R.id.bt_calljs){

        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
