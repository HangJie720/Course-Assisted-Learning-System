package com.ijustyce.fastandroiddev.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public  class QRCode{

    // 插入到二维码里面的图片对象
    private Bitmap mBitmap;
    private int logoWidth, logoHeight, QRWidth, QRHeight, logoRes;
    private Activity activity;

    public QRCode(Activity activity, int logoWidth, int logoHeight, int QRWidth, int QRHeight, int logoRes){

        this.logoHeight = logoHeight;
        this.logoRes = logoRes;
        this.logoWidth = logoWidth;
        this.QRHeight = QRHeight;
        this.QRWidth = QRWidth;
        this.activity = activity;

        init();
    }

    private void init() {

        mBitmap = BitmapFactory.decodeResource(activity.getResources(), logoRes);
        // 缩放图片
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        Matrix m = new Matrix();
        float sx = (float) 2 * logoWidth / width;
        float sy = (float) 2 * logoHeight / height;
        m.setScale(sx, sy);
        // 重新构造一个图片
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                width, height, m, false);
        Log.i("===width===", "" + mBitmap.getWidth());
        Log.i("===height===", "" + mBitmap.getHeight());
    }

    /**
     * 生成二维码
     *
     * @return Bitmap
     * @throws WriterException
     */
    public Bitmap cretaeBitmap(String str) {

        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);

        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
//		    BitMatrix matrix= new MultiFormatWriter().encode(str,
//				BarcodeFormat.QR_CODE, 300, 300);
        BitMatrix matrix = null;
        try{
            matrix  = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, QRWidth, QRHeight, hints);
        }catch (WriterException e){
            e.printStackTrace();
            return null;
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        Log.i("===width===", "" + width);
        Log.i("===height===", "" + height);
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int halfW = width / 2;
        int halfH = height / 2;
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x > halfW - logoWidth && x < halfW + logoWidth
                        && y > halfH - logoHeight
                        && y < halfH + logoHeight) {
                    pixels[y * width + x] = mBitmap.getPixel(x - halfW
                            + logoWidth, y - halfH + logoHeight);
                } else {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    }else {                              //无信息设置像素点为白色
                        pixels[y * width + x] = 0xffffffff;
                    }
                }

            }
        }

        //  not add logo
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (matrix.get(x, y)) {
//                    pixels[y * width + x] = 0xff000000;
//                } else {
//                    pixels[y * height + x] = 0xffffffff;
//                }
//            }
//        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * save Bitmap to picPath
     * @param mBitmap
     * @param picPath
     */
    public void saveBitmap(Bitmap mBitmap , String picPath) {

        File pic = new File(picPath);
        if (!pic.exists()) {
            try {
                pic.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(pic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}