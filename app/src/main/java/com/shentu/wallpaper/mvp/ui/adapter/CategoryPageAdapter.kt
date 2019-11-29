package com.shentu.wallpaper.mvp.ui.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.ui.fragment.CategoryPageItemFragment

@SuppressLint("WrongConstant")
class CategoryPageAdapter(private val sharePos: Int, val data: MutableList<Wallpaper>, fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

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