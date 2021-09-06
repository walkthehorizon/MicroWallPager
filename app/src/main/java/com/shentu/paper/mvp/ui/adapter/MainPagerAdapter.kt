package com.shentu.paper.mvp.ui.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shentu.paper.mvp.ui.home.TabHomeFragment
import com.shentu.paper.mvp.ui.my.TabMyFragment

@SuppressLint("WrongConstant")
class MainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    val fragments  = listOf<Fragment>(TabHomeFragment.newInstance(),TabMyFragment.newInstance())

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
