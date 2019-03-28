package com.shentu.wallpaper.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shentu.wallpaper.app.Constant;
import com.shentu.wallpaper.mvp.ui.home.TabHomeFragment;
import com.shentu.wallpaper.mvp.ui.fragment.MyFragment;

import java.util.Arrays;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = Arrays.asList(TabHomeFragment.Companion.newInstance()
                , MyFragment.Companion.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return Constant.MAIN_TAB_TITLES.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return Constant.MAIN_TAB_TITLES.get(position);
    }
}
