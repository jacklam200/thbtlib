package com.lingzhi.retail.btlib.print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 *  打印图片
 * @author linzaixiong
 * @version 1.0
 * @updated  2019/8/1
 */
public class PrintPicture {

    public Canvas mCanvas = null;
    public Paint mPaint = null;
    public Bitmap mBitmap = null;
    public int mWidth;
    public float mLength = 0.0F;
    public byte[] mBitBuf = null;

    public PrintPicture() {
    }


	/**
	 *  获取长度
	 * @return
	 */
	public int getLength() {
        return (int) this.mLength;
    }

	/**
	 *  初始化要打印的图片
	 * @param bitmap bitmap图片
	 */
	public void init(Bitmap bitmap) {

        if (null != bitmap) {
            initCanvas(bitmap.getWidth());
        }
        if (null == mPaint) {
            initPaint();
        }
        if (null != bitmap) {
            drawImage(0, 0, bitmap);
        }
    }

	/**
	 *  初始化要打印的图片
	 * @param bitmap 打印图片
	 * @param width 宽
	 * @param height 高
	 */
	public void init(Bitmap bitmap, int width, int height) {
        if (null != bitmap) {
            initCanvas(width, height);
        }
        if (null == mPaint) {
            initPaint();
        }
        if (null != bitmap) {
            drawImage(0, 0, bitmap);
        }
    }

	/**
	 *  初始化好要打印的canvas
	 * @param width 只带宽度
	 */
	public void initCanvas(int width) {
        int h = 10 * width;
        this.mBitmap = Bitmap.createBitmap(width, h, Bitmap.Config.RGB_565);
        this.mCanvas = new Canvas(this.mBitmap);
        this.mCanvas.drawColor(-1);
        this.mWidth = width;
        this.mBitBuf = new byte[this.mWidth / 8];
    }

	/**
	 *  初始化画布
	 * @param w  宽
	 * @param height 高
	 */
	public void initCanvas(int w, int height) {
        int h = height;
        this.mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        this.mCanvas = new Canvas(this.mBitmap);
        this.mCanvas.drawColor(-1);
        this.mWidth = w;
        this.mBitBuf = new byte[this.mWidth / 8];
    }

	/**
	 *  初始化paint
	 */
	public void initPaint() {
	    // 新建一个画笔
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-16777216);

    }


	/**
	 * 画图
	 * @param x
	 * @param y
	 * @param btm
	 */
	public void drawImage(float x, float y, Bitmap btm) {
        try {
            this.mCanvas.drawBitmap(btm, x, y, null);
            if (this.mLength < y + btm.getHeight()) {
	            this.mLength = (y + btm.getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != btm) {
                btm.recycle();
            }
        }
    }

    /**
     * 使用光栅位图打印
     *
     * @return 字节
     */
    public byte[] printDraw() {

        Bitmap nbm = Bitmap
                .createBitmap(this.mBitmap, 0, 0, this.mWidth, getLength());

        byte[] imgBuf = new byte[this.mWidth / 8 * getLength() + 8];

        int s = 0;

        // 前8位是打印图片命令

        // 打印光栅位图的指令
	    // 十六进制0x1D
	    imgBuf[0] = 29;
	    // 十六进制0x76
	    imgBuf[1] = 118;
	    // 30
	    imgBuf[2] = 48;
	    // 位图模式 0,1,2,3
	    imgBuf[3] = 0;
        // 表示水平方向位图字节数（xL+xH × 256）
	    imgBuf[4] = (byte) (this.mWidth / 8);
	    imgBuf[5] = 0;
        // 表示垂直方向位图点数（ yL+ yH × 256）
	    imgBuf[6] = (byte) (getLength() % 256);
	    imgBuf[7] = (byte) (getLength() / 256);

        s = 7;
	    // 循环位图的高度
        for (int i = 0; i < getLength(); i++) {
	        // 循环位图的宽度
            for (int k = 0; k < this.mWidth / 8; k++) {
	            // 返回指定坐标的颜色
                int c0 = nbm.getPixel(k * 8 + 0, i);
                int p0;
	            // 判断颜色是不是白色
                if (c0 == -1) {
	                // 0,不打印该点
	                p0 = 0;
                }
                else {
	                // 1,打印该点
                    p0 = 1;
                }
                int c1 = nbm.getPixel(k * 8 + 1, i);
                int p1;
                if (c1 == -1) {
	                p1 = 0;
                }
                else {
                    p1 = 1;
                }
                int c2 = nbm.getPixel(k * 8 + 2, i);
                int p2;
                if (c2 == -1) {
	                p2 = 0;
                }
                else {
                    p2 = 1;
                }
                int c3 = nbm.getPixel(k * 8 + 3, i);
                int p3;
                if (c3 == -1) {
	                p3 = 0;
                }
                else {
                    p3 = 1;
                }
                int c4 = nbm.getPixel(k * 8 + 4, i);
                int p4;
                if (c4 == -1) {
	                p4 = 0;
                }
                else {
                    p4 = 1;
                }
                int c5 = nbm.getPixel(k * 8 + 5, i);
                int p5;
                if (c5 == -1) {
	                p5 = 0;
                }
                else {
                    p5 = 1;
                }
                int c6 = nbm.getPixel(k * 8 + 6, i);
                int p6;
                if (c6 == -1) {
	                p6 = 0;
                }
                else {
                    p6 = 1;
                }
                int c7 = nbm.getPixel(k * 8 + 7, i);
                int p7;
                if (c7 == -1) {
	                p7 = 0;
                }
                else {
                    p7 = 1;
                }
                int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8
                        + p5 * 4 + p6 * 2 + p7;
                this.mBitBuf[k] = (byte) value;
            }

            for (int t = 0; t < this.mWidth / 8; t++) {
                s++;
	            imgBuf[s] = this.mBitBuf[t];
            }
        }

        if (null != this.mBitmap) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }

        return imgBuf;
    }
}