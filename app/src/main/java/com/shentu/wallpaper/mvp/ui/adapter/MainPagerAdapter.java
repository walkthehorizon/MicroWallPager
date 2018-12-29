package com.shentu.wallpaper.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shentu.wallpaper.mvp.ui.fragment.HotPagerFragment;
import com.shentu.wallpaper.mvp.ui.fragment.MyFragment;

import java.util.Arrays;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<String> items;
    private List<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        items = Arrays.asList("推荐", "分类", "排行", "我的");
        fragments = Arrays.asList(HotPagerFragment.newInstance()
                , MyFragment.Companion.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return items.get(position);
    }
}
