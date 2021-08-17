package com.shentu.paper.mvp.ui.activity

import android.os.Bundle
import com.micro.base.BaseActivity
import com.shentu.paper.R
import com.shentu.paper.mvp.presenter.MainPresenter

class TestMMKVActivity : BaseActivity<MainPresenter>() {
    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {



    }
}