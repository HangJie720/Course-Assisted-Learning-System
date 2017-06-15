package com.ijustyce.fastandroiddev.baseLib.crash;

import android.content.Context;

import com.ijustyce.fastandroiddev.baseLib.BuildConfig;
import com.ijustyce.fastandroiddev.baseLib.utils.FileUtils;
import com.ijustyce.fastandroiddev.baseLib.utils.ILog;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by yc on 16-1-28. 异常捕获类
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler INSTANCE = new CrashHandler();

    private Context mContext;
    private long lastCrash;
    private static final long CRASH_DELAY = 5000;    //  5秒内仅处理一次，即使5秒内崩溃了多次，也只处理一次
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当 UncaughtException 发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        ex.printStackTrace();
        saveToFile(ex);
        if (BuildConfig.DEBUG && mDefaultHandler != null){
            mDefaultHandler.uncaughtException(thread, ex);
        }else{
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private void saveToFile(Throwable ex){

        if (System.currentTimeMillis() - lastCrash < CRASH_DELAY){
            return ;
        }

        lastCrash = System.currentTimeMillis();

        String path = FileUtils.getAvailablePath(mContext, "crash") + "/crash/";
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        path += + System.currentTimeMillis() + ".txt";
        ILog.i("===crash===", "saved to " + path);
        FileUtils.writeTextFile(new File(path), getCrashInfo(ex));
    }

    /**
     * 保存错误信息到文件中
     * *
     *
     * @param ex Throwable
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String getCrashInfo(Throwable ex) {

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        return writer.toString();
    }
}
