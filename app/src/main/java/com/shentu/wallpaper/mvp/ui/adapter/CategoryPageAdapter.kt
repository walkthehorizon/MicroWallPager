package com.shentu.wallpaper.mvp.ui.adapter

import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.ui.fragment.CategoryPageItemFragment

class CategoryPageAdapter(private val sharePos: Int, val data: MutableList<Wallpaper>, fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return CategoryPageItemFragment.newInstance(data[position].url,sharePos == position)
    }

    override fun getItemPosition(`object`: Any): Int {
        return androidx.viewpager.widget.PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "图$position"
    }
}