package com.ijustyce.fastandroiddev.base;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import com.ijustyce.fastandroiddev.R;
import com.ijustyce.fastandroiddev.baseLib.utils.ILog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by yc on 15-12-25.   顶部是tab的Activity
 */
public abstract class BaseTopTabActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private BaseViewPager mViewPager;

    public List<String> mTitleList;
    public List<Fragment> mFragmentList;

    private LinearLayout headerView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_top_tab;
    }

    protected final void doInit(){

        initData();

        addTitle();
        addFragment();
        setAdapter();
    };

    public final void addHeaderView(View view){

        if (view != null) {
            headerView.addView(view);
        }
    }

    private void initData(){

        mTabLayout = (TabLayout) findViewById(R.id.tabTitle);
        headerView = (LinearLayout) findViewById(R.id.headerView);
        mViewPager = (BaseViewPager) findViewById(R.id.viewPager);
        mFragmentList = new ArrayList<>();
        mTitleList = new ArrayList<>();
    }

    /**
     * 添加tab 标题
     */
    public abstract void addTitle();

    /**
     * 添加fragment
     */
    public abstract void addFragment();

    /**
     *  用adapter 关联起来
     */
    private void setAdapter() {

        setTabIndicatorColor(R.color.colorPrimaryDark);
        setTabBackground(R.color.home_tab);
        setTabTextColor(R.color.textColor, R.color.colorPrimaryDark);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
                mFragmentList, mTitleList);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size() -1 );

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onDestroy(){

        ButterKnife.unbind(this);
        super.onDestroy();
    }

    public void setScrollMode(){

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    public String getResString(int resId){

        return getResources().getString(resId);
    }

    public void setTabBackground(int color){

        mTabLayout.setBackgroundColor(getResColor(color));
    }

    public void setTabIndicatorHeight(int height){

        mTabLayout.setSelectedTabIndicatorHeight(height);
    }

    private int getResColor(int color){

        return getResources().getColor(color);
    }

    /**
     *  设置tab的可见性
     * @param value 只能是 View.GONE、View.VISIBLE、View.INVISIBLE
     */
    public void setTabVisibility(int value){

        if (value == View.GONE || value == View.VISIBLE || value == View.INVISIBLE) {
            mTabLayout.setVisibility(value);
        }else{
            ILog.e("===BaseTabFragment===", "setTabVisibility, value only can be " +
                    "View.GONE, View.VISIBLE or View.INVISIBLE");
        }
    }

    public void setCurrentFragment(int id){

        if (id > -1 && id < mFragmentList.size() && mViewPager != null){
            mViewPager.setCurrentItem(id, true);
        }else{
            ILog.e("===BaseTabFragment===", "setCurrentFragment, id overflow or mViewPager is null");
        }
    }

    /**
     * 是否禁止左右滑动
     * @param canScroll   TRUE的时候可以左右滑动，false的时候不能左右滑动，默认是true
     */
    public void setCanScroll(boolean canScroll){

        if (mViewPager != null){
            mViewPager.setCanScroll(canScroll);
        }
    }

    public void setTabIndicatorColor(int color){

        mTabLayout.setSelectedTabIndicatorColor(getResColor(color));
    }

    public void setTabTextColor(int normalColor, int selectedColor){

        mTabLayout.setTabTextColors(getResColor(normalColor), getResColor(selectedColor));
    }
}
