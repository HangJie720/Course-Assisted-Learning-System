package com.ijustyce.fastandroiddev.umenglib;

import android.content.Context;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

/**
 * Created by yc on 15-12-26.   umeng自动更新的封装
 */
public class UpdateTool {

    /**
     * 检查更新并弹出提示dialog
     * @param context context
     * @param autoDownload 是否静默下载更新包
     */
    public synchronized static void update(Context context, boolean autoDownload){

        UmengUpdateAgent.update(context);
        if (autoDownload){
            UmengUpdateAgent.silentUpdate(context);
        }
    }

    /**
     * 仅在wifi下更新
     * @param value
     */
    public synchronized static void updateOnlyWifi(boolean value){

        UmengUpdateAgent.setUpdateOnlyWifi(value);
    }

    /**
     * 检查更新，如果当前是wifi则自动下载更新
     * @param context
     */
    public synchronized static void update(Context context){

        updateOnlyWifi(true);
        UmengUpdateAgent.silentUpdate(context);
    }

    public synchronized static void forceUpdate(final Context context){

        if (context == null){
            return;
        }

        UmengUpdateAgent.forceUpdate(context);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {

                if (updateResponse == null || !updateResponse.hasUpdate){
                    Toast.makeText(context, "您已安装最新版本!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
