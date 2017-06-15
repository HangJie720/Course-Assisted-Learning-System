package com.ijustyce.fastandroiddev.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ijustyce.fastandroiddev.R;
import com.ijustyce.fastandroiddev.baseLib.utils.IJson;
import com.ijustyce.fastandroiddev.net.HttpListener;
import com.ijustyce.fastandroiddev.net.VolleyUtils;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by yc on 2015/8/14.  baseFragment for all fragment
 */
public abstract class BaseFragment<T> extends Fragment {

    public Context mContext;
    public View mView;
    public SweetAlertDialog dialog;
    public Handler handler;

    public String TAG ;
    private T mData;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        if (mView != null) {
            return mView;
        }

        TAG = getClass().getName();
        mView = inflater.inflate(getLayoutId(), container, false);

        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }

        mContext = getActivity();
        ButterKnife.bind(this, mView);
        handler = new Handler();
        doInit();
        afterCreate();
        return mView;
    }

    public abstract int getLayoutId();

    public void afterCreate() {
    }

    void doInit(){}

    public void doResume() {
    }

    @Override
    public final void onResume() {
        super.onResume();

        if (mView == null) {
            return;
        }
        if (mContext != null) {
            doResume();
            return;
        }
        doResume();
    }

    public String getResString(int id) {

        return getResources().getString(id);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
        if (mContext != null && TAG != null && VolleyUtils.getInstance()
                .getVolleyRequestQueue(mContext) != null){
            VolleyUtils.getInstance().getVolleyRequestQueue(mContext).cancelAll(TAG);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dismiss();
        if (mContext != null && TAG != null && VolleyUtils.getInstance()
                .getVolleyRequestQueue(mContext) != null){
            VolleyUtils.getInstance().getVolleyRequestQueue(mContext).cancelAll(TAG);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mContext != null && TAG != null && VolleyUtils.getInstance()
                .getVolleyRequestQueue(mContext) != null){
            VolleyUtils.getInstance().getVolleyRequestQueue(mContext).cancelAll(TAG);
        }
        dismiss();
        ButterKnife.unbind(this);
        if (mContext != null) {
            mContext = null;
        }
        if (httpListener != null) {
            httpListener = null;
        }
    }

    private Runnable dismiss = new Runnable() {
        @Override
        public void run() {

            if (dialog != null && dialog.isShowing() && mContext != null) {

                dialog.cancel();
            }
        }
    };

    /**
     * 让dialog消失
     * @param delay 0 - 5000 大于 5000 按5000计算，小于0按0计算
     */
    public void dismiss(int delay){

        if (handler == null){
            handler = new Handler();
        }
        if (delay <= 0){
            handler.post(dismiss);
            return;
        }if (delay > 10000){
            delay = 5000;
        }
        handler.postDelayed(dismiss, delay);
    }

    public void dismiss() {

        dismiss(0);
    }

    public String getTAG() {
        return TAG;
    }

    public void showProcess(String text){

        dialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(text);
        dialog.getProgressHelper().setBarColor(R.color.colorAccent);
        dialog.show();
    }

    public void showProcess(int resId) {

        dialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText(getResString(resId));
        dialog.getProgressHelper().setBarColor(R.color.colorAccent);
        dialog.show();
    }

    public void newActivity(Intent intent, Bundle bundle){

        if (intent == null){
            return;
        }
        if (bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    //  你可以在这里加入界面切换的动画，或者统计页面等
    public void newActivity(Intent intent) {

        newActivity(intent, null);
    }

    public void newActivity(Class gotoClass) {

        newActivity(new Intent(mContext, gotoClass), null);
    }

    public void newActivity(Class gotoClass, Bundle bundle) {

        newActivity(new Intent(mContext, gotoClass), bundle);
    }

    public HttpListener httpListener = new HttpListener() {

        @Override
        public void fail(int code, String msg, String taskId) {

            dismiss();
            if (mContext == null) {
                return;
            }
            onFailed(code, msg, taskId);
        }

        @Override
        public void success(String object, String taskId) {

            dismiss();
            if (mContext == null) {
                return;
            }
            Class type = getType();
            if (type != null) {
                mData = IJson.fromJson(object, getType());
            }
            onSuccess(object, taskId);
        }
    };

    public T getData(){

        return mData;
    }

    public Class getType(){

        return null;
    }

    public void onSuccess(String object, String taskId) {

        //  TODO 覆写这个方法，以获取http响应
    }

    public void onFailed(int code, String msg, String taskId) {

        //  ToastUtil.show(mContext, msg);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("FOR_BUG", "FOR_BUG");
        super.onSaveInstanceState(outState);
    }
}
