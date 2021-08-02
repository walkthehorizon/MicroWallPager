package com.shentu.paper.mvp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.micro.base.BaseActivity
import com.micro.mvp.IPresenter
import com.shentu.paper.R
import com.shentu.paper.mvp.ui.adapter.PaperSummaryVpAdapter
import kotlinx.android.synthetic.main.activity_paper_summary.*

class PaperSummaryActivity : BaseActivity<IPresenter>() {

    private val fragments: MutableList<Fragment> = mutableListOf()

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