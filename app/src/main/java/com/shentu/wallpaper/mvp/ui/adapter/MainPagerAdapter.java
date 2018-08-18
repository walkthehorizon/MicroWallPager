package com.shentu.wallpaper.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shentu.wallpaper.mvp.ui.fragment.CategoryFragment;
import com.shentu.wallpaper.mvp.ui.fragment.HotPagerFragment;
import com.shentu.wallpaper.mvp.ui.fragment.MyFragment;
import com.shentu.wallpaper.mvp.ui.fragment.RankFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<String> items;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        items = Arrays.asList("推荐", "分类", "排行", "我的");
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return CategoryFragment.Companion.newInstance();
        }
        if (position == 2) {
            return RankFragment.Companion.newInstance();
        }
        if (position == 3) {
            return MyFragment.Companion.newInstance();
        }
        return HotPagerFragment.newInstance();
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
