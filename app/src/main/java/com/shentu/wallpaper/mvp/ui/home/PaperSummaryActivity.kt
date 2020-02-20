package com.shentu.wallpaper.mvp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.shentu.wallpaper.R
import com.shentu.wallpaper.mvp.ui.adapter.PaperSummaryVpAdapter
import kotlinx.android.synthetic.main.activity_paper_summary.*

class PaperSummaryActivity : BaseActivity<IPresenter>() {

    private val fragments: MutableList<Fragment> = mutableListOf()

    override fun setupActivityComponent(appComponent: AppComponent) {

    }

    override fun initData(savedInstanceState: Bundle?) {
        fragments.add(HomeNewFragment.newInstance())
        fragments.add(HomeRankFragment.newInstance())
        summaryVp.adapter = PaperSummaryVpAdapter(supportFragmentManager, fragments)
        tabPaper.setupWithViewPager(summaryVp)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_paper_summary
    }
}