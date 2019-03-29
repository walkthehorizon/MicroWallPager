package com.shentu.wallpaper.mvp.ui.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
