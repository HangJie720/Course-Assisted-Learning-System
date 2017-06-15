package com.ijustyce.fastandroiddev.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.ijustyce.fastandroiddev.R;
import com.ijustyce.fastandroiddev.baseLib.callback.CallBackManager;
import com.ijustyce.fastandroiddev.manager.AppManager;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by yc on 15-12-25.   引导页的封装
 */
public abstract class BaseGuideActivity extends AutoLayoutActivity {

    private ViewPager mViewPager;

    public List<Fragment> mFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        AppManager.pushActivity(this);

        afterCreate();
        initData();
        addFragment();
        setAdapter();

        CallBackManager.getActivityLifeCall().onCreate(this);
    }

    public void afterCreate(){}

    private int getResColor(int color){

        return getResources().getColor(color);
    }

    private void initData(){

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mFragmentList = new ArrayList<>();
    }

    /**
     * 添加fragment
     */
    public abstract void addFragment();

    /**
     *  用adapter 关联起来
     */
    private void setAdapter() {

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
                mFragmentList, null);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size() -1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        AppManager.moveActivity(this);
        CallBackManager.getActivityLifeCall().onDestroy(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        CallBackManager.getActivityLifeCall().onStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CallBackManager.getActivityLifeCall().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CallBackManager.getActivityLifeCall().onPause(this);
    }
}
