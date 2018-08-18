package com.shentu.wallpaper.mvp.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.ui.fragment.CategoryPageItemFragment

class CategoryPageAdapter(private val sharePos: Int, val data: MutableList<Wallpaper>, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return CategoryPageItemFragment.newInstance(data[position].small_url,sharePos == position)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "图$position"
    }
}