package com.ijustyce.fastandroiddev.baseLib.callback;

import android.app.Activity;

/**
 * Created by yc on 16-2-7. Activity    生命周期调用
 */
public interface ActivityLifeCall {

    void onResume(Activity activity);
    void onPause(Activity activity);
    void onCreate(Activity activity);
    void onStop(Activity activity);
    void onDestroy(Activity activity);

    class DefaultActivityLifeCall implements ActivityLifeCall{

        @Override
        public void onResume(Activity activity) {

        }

        @Override
        public void onPause(Activity activity) {

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
}
