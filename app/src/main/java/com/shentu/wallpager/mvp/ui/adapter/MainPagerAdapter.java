package com.shentu.wallpager.mvp.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shentu.wallpager.mvp.ui.fragment.HotPagerFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return HotPagerFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 4;
    }
}
