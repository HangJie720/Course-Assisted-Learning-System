package com.ijustyce.fastandroiddev.baseLib.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yc on 16-2-6. 再次封装的LogCat类，实现在release版本不打印logcat
 */
public class ILog {

    private static boolean showLog = true;  //  是否显示，用于release版本不打印log
    private static String tag = "FastAndroidDev";   //  默认的

    public static void i(@NonNull String tag, @NonNull String msg) {

        if (showLog) {
            Log.i(tag, msg);
        }
    }

    public static void i(@NonNull String msg) {

        if (showLog) {
            Log.i(tag, msg);
        }
    }

    public static void d(@NonNull String tag, @NonNull String msg) {

        if (showLog) {
            Log.d(tag, msg);
        }
    }

    public static void d(@NonNull String msg) {

        if (showLog) {
            Log.d(tag, msg);
        }
    }

    public static void e(@NonNull String tag, @NonNull String msg) {

        if (showLog) {
            Log.e(tag, msg);
        }
    }

    public static void e(@NonNull String msg) {

        if (showLog) {
            Log.e(tag, msg);
        }
    }

    public static boolean isShowLog() {
        return showLog;
    }

    public static void setShowLog(boolean showLog) {
        ILog.showLog = showLog;
    }
}
