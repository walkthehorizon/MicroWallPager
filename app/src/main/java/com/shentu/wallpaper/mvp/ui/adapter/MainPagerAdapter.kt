package com.shentu.wallpaper.mvp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shentu.wallpaper.mvp.ui.fragment.CategoryFragment
import com.shentu.wallpaper.mvp.ui.fragment.MyFragment
import com.shentu.wallpaper.mvp.ui.home.TabHomeFragment

class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val titles: List<String> = listOf("推荐", "分类", "我的")
    private val fragments: List<Fragment> = listOf(TabHomeFragment.newInstance()
            , CategoryFragment.newInstance(), MyFragment.newInstance())

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}
