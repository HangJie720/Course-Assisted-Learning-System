package com.ijustyce.fastandroiddev.baseLib.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by yc on 16-2-6. 文件操作类
 */
public class FileUtils {

    /**
     *  递归计算文件夹下面的文件的大小，以M为单位,结果保留两位小数(不四舍五入)
     * @param file 文件夹或者文件名
     * @return 文件大小 以M 为单位
     */
    public static double getDirSize(@NonNull File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return CommonTool.getShortDouble(size / 1024 / 1024);
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                return CommonTool.getShortDouble(file.length() / 1024/ 1024);
            }
        } else {
            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }

    /**
     * 调用文件管理器显示某个文件
     * @param path  文件路径
     * @param context   Context
     * @return  如果参数有误或者文件不存在，返回0，如果没有文件管理器返回-1，如果成功返回1
     */
    public static int showFile(String path, Context context) {

        if (path == null || context == null || !new File(path).exists()){

            return 0;
        }

        Uri selectedUri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");

        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            context.startActivity(intent);
            return 1;
        }
        return -1;
    }

    /**
     * 获取可用的 文件目录，先尝试sdcard、然后尝试内部存储空间，最后则是data目录了，如果是sdcard，则会创建name这个文件夹
     * @param context context
     * @param name 文件夹名字,如果sdcard可用，会在sdcard创建这个目录
     * @return 成功返回true，失败返回false
     */
    public static String getAvailablePath(@NonNull Context context, @NonNull String name){

        File f = context.getExternalFilesDir(null);
        if (f == null) {
            f = Environment.getExternalStorageDirectory();
            if (f == null) {
                f = context.getFilesDir();
            } else {
                f = new File(f.getAbsolutePath() + "/" + name + "/");
                f.mkdirs();
            }
        }
        return f == null ? null : f.getAbsolutePath();
    }

    /**
     * 复制文件
     *
     * @param oldPath 要复制的文件路径
     * @param newPath 目标文件路径
     * @return 成功返回true，失败返回false
     */
    public static boolean copyFile(@NonNull String oldPath, @NonNull String newPath) {
        try {
            int bytesum = 0;
            int byteread;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
        return true;
    }

    /**
     * 复制 assets 下的文件到 toPath 目录
     *
     * @param context  mContext
     * @param toPath   目标文件路径
     * @param fileName assets 下的文件名称
     * @return 成功返回true，失败返回false
     */
    public static boolean copyDataToSD(@NonNull Context context, @NonNull String toPath, @NonNull String fileName) {

        try {
            InputStream myInput;
            OutputStream myOutput;
            myOutput = new FileOutputStream(toPath);
            myInput = context.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 保存bitmap 为 jpg文件
     *
     * @param mBitmap bitmap
     * @param bitName 文件路径 , 必须 .jpg 结尾 比如 /sdcard/tmp/1.jpg
     */
    public static boolean savBitmapToPng(@NonNull Bitmap mBitmap, @NonNull String bitName) {
        File f = new File(bitName);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 保存 bitmap 为 .jpg 文件
     *
     * @param mBitmap bitmap
     * @param bitName 文件路径 , 必须以 .jpg 结尾 比如 /sdcard/tmp/1.jpg
     */
    public static boolean savBitmapToJpg(@NonNull Bitmap mBitmap, @NonNull String bitName) {

        File f = new File(bitName);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取文件
     *
     * @param file  文本文件
     * @return  文件内容 发生异常时，返回null
     */
    public static String readTextFile(@NonNull File file) {

        String text = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            text = readTextInputStream(is);
        }catch (IOException e){
            e.printStackTrace();
        } finally{
            try {
                if (is != null) {
                    is.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return text;
    }

    /**
     * 从流中读取文件
     *
     * @param is    流对象
     * @return  文件内容
     */
    public static String readTextInputStream(@NonNull InputStream is){

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\r\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 将文本内容写入文件
     *
     * @param file  要写入的文件
     * @param str   要写入的内容
     */
    public static void writeTextFile(@NonNull File file, @NonNull String str) {

        DataOutputStream out;
        try {
            out = new DataOutputStream(new FileOutputStream(file));
            out.write(str.getBytes());
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 遍历删除文件 如果是目录，则删除目录下的一切内容，目录不会删除，如果是文件，则删除文件
     *
     * @param path
     */
    public static void deleteFile(@NonNull String path) {

        ILog.i("===path===", "path is " + path);
        if (StringUtils.isEmpty(path)) {
            ILog.e("===path is null , return ...===");
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            ILog.e("===file not exists , return ...===");
            return;
        }
        if (file.isFile()) {
            file.delete();
        }
        if (file.isDirectory()) {
            for (File delete : file.listFiles()) {
                deleteFile(delete.getAbsolutePath());
            }
        }
    }

    /**
     * 返回选择文件时 选定的文件路径
     * @param context   context
     * @param uri   onActivityResult里 Intent 对象 getData
     * @return  如果存在，返回路径，否则返回null
     */
    public static String getPath(Context context, Uri uri) {

        if (context == null || uri == null){
            return null;
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = context.getContentResolver() == null ?
                        null : context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor == null){
                    return null;
                }
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
}
