package com.ijustyce.fastandroiddev.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 这里使用多个fragment进行切换，每个fragment 都有自己独立的代码
 * Created by yc on 15-12-25.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> titleList;

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList,
                           List<String> titleList) {

        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override

    public Fragment getItem(int i) {

        return fragmentList.get(i);
    }

    @Override
    public int getCount() {

        return fragmentList.size();
    }

    /**
     * 此方法是给tablayout中的tab赋值的，就是显示名称
     *
     * @param position
     * @return
     */

    @Override
    public CharSequence getPageTitle(int position) {

        return titleList.get(position % titleList.size());
    }
}
