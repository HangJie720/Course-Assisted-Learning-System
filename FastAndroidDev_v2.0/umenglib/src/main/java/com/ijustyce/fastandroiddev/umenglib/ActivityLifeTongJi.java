package com.ijustyce.fastandroiddev.umenglib;

import android.app.Activity;
import android.util.Log;

import com.ijustyce.fastandroiddev.baseLib.callback.ActivityLifeCall;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by yc on 16-2-7. umeng 统计 你可以在你的Application类里设置：
 *      CallBackManager.setActivityLifeCall(new ActivityLifeTongJi());
 *      如果你有多个类似的需求，可以自己写一起，或者通过继承，这里只是umeng统计
 */
public class ActivityLifeTongJi implements ActivityLifeCall {

    @Override
    public void onResume(Activity activity) {
        Log.i("===umeng===", "onResume...");
        MobclickAgent.onResume(activity);
    }

    @Override
    public void onPause(Activity activity) {
        Log.i("===umeng===", "onResume...");
        MobclickAgent.onPause(activity);
    }

    @Override
    public void onCreate(Activity activity) {

    }

    @Override
    public void onStop(Activity activity) {

    }

    @Override
    public void onDestroy(Activity activity) {

    }
}
