package com.ijustyce.fastandroiddev.baseLib.callback;

/**
 * Created by yc on 16-2-7. 回调管理
 */
public class CallBackManager {

    private static ActivityLifeCall activityLifeCall;

    public static ActivityLifeCall getActivityLifeCall(){

        return activityLifeCall == null ? new ActivityLifeCall.DefaultActivityLifeCall() : activityLifeCall;
    }

    public static void setActivityLifeCall(ActivityLifeCall activityLifeCall){

        CallBackManager.activityLifeCall = activityLifeCall;
    }
}