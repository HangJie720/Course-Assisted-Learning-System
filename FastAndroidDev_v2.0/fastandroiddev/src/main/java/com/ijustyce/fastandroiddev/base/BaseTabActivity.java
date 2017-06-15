package com.ijustyce.fastandroiddev.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ijustyce.fastandroiddev.R;
import com.ijustyce.fastandroiddev.baseLib.utils.ILog;
import com.ijustyce.fastandroiddev.baseLib.callback.CallBackManager;
import com.ijustyce.fastandroiddev.manager.AppManager;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by yc on 15-12-25.   底部是tab的activity的父类
 */
public abstract class BaseTabActivity extends AutoLayoutActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TextView label;

    private Toolbar toolbar;

    public List<String> mTitleList;
    public List<Fragment> mFragmentList;
    private List<RadioButton> mRadioButton;

    private Handler handler;
    private boolean isPressed;
    private static final int DELAY = 2000, SHORT_DELAY = 1000;

    private boolean canClick = true;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        AppManager.pushActivity(this);
        initData();

        addFragment();
        addTitle();
        setAdapter();

        handler = new Handler();
        CallBackManager.getActivityLifeCall().onCreate(this);
        afterCreate();
    }

    private Runnable checkExit = new Runnable() {
        @Override
        public void run() {

            isPressed = false;
        }
    };

    public final void showToolBar(boolean isShow) {

        toolbar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public final void backPress() {

        if (isPressed) {
            this.finish();
        } else {
            isPressed = true;
            Toast.makeText(this, R.string.hint_exit, Toast.LENGTH_LONG).show();
            if (handler == null) {
                handler = new Handler();
            }
            handler.postDelayed(checkExit, DELAY);
        }
    }

    private void initData() {

        mTabLayout = (TabLayout) findViewById(R.id.tabTitle);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        label = (TextView) findViewById(R.id.label);
        mFragmentList = new ArrayList<>();
    }

    public final int getCurrentTab() {

        return mViewPager == null ? 0 : mViewPager.getCurrentItem();
    }

    private void addTitle() {

        mTitleList = new ArrayList<>();
        mTitleList.add("tmp");
        mTitleList.add("tmp");
        mTitleList.add("tmp");
        mRadioButton = new ArrayList<>(mFragmentList.size());
    }

    /**
     * 添加fragment
     */
    public abstract void addFragment();

    public abstract void afterCreate();

    public void onPageSelect(int position) {
    }

    /**
     * 用adapter 关联起来
     */
    private void setAdapter() {

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
                mFragmentList, mTitleList);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size() - 1);

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                selectTab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state == ViewPager.SCROLL_STATE_IDLE) {

                    selectTab();    //  这句才是关键，滚动结束后调用
                    canClick = true;    //  canClick 仅仅是为了防止多处点击
                }
            }
        });
    }

    private void selectTab() {

        int position = mViewPager.getCurrentItem();
        if (position >= mRadioButton.size()) {
            return;
        }

        for (RadioButton tmp : mRadioButton) {
            tmp.setChecked(false);
        }
        RadioButton tmp = mRadioButton.get(position);
        tmp.setChecked(true);
        label.setText(tmp.getText());

        onPageSelect(position);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        ButterKnife.unbind(this);
        AppManager.moveActivity(this);
        CallBackManager.getActivityLifeCall().onDestroy(this);
    }

    public final void addTab(int layoutId, int radioButtonId) {

        if (mTitleList == null || mRadioButton == null) {

            ILog.e("===mTitleList or mRadioButton is null ...return...");
            return;
        }

        final int tabId = mRadioButton.isEmpty() ? 0 : mRadioButton.size();

        final TabLayout.Tab mTab = mTabLayout.getTabAt(tabId);
        if (mTab == null) {
            return;
        }

        View tab = LayoutInflater.from(this).inflate(layoutId, null);
        RadioButton button = (RadioButton) tab.findViewById(radioButtonId);
        button.setChecked(mRadioButton.isEmpty());

        if (mRadioButton.isEmpty()) {
            label.setText(button.getText());
        }

        //  必须要在button 上添加监听,view上不起作用
        button.setId(tabId);
        button.setOnClickListener(onClick);
        mTab.setCustomView(tab);
        mRadioButton.add(button);
    }

    private Runnable enAbleClick = new Runnable() {
        @Override
        public void run() {
            canClick = true;
        }
    };

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ILog.i("===tab click===");
            if (view == null || !canClick) {
                ILog.e("===view is null or click not finish===");
                selectTab();    //  及时修复页面显示，而不是等到滚动结束
                return;
            }
            canClick = false;
            mViewPager.setCurrentItem(view.getId(), true);
            handler.postDelayed(enAbleClick, SHORT_DELAY);  //  为防止万一，1秒后允许再次点击
        }
    };

    @Override
    public final boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            backPress();
        }
        return true;
    }

    public final void newActivity(Class className) {

        startActivity(new Intent(this, className));
    }

    public final void setScrollMode() {

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        CallBackManager.getActivityLifeCall().onStop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CallBackManager.getActivityLifeCall().onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CallBackManager.getActivityLifeCall().onResume(this);
    }
}