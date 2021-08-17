package com.shentu.paper.mvp.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.ToastUtils
import com.micro.base.BaseActivity
import com.shentu.paper.R
import com.shentu.paper.mvp.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_chip_test.*
import timber.log.Timber

class TestChipActivity : BaseActivity<MainPresenter>() {

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_chip_test
    }

    override fun initData(savedInstanceState: Bundle?) {
        tvHead.setOnClickListener {
            ToastUtils.showShort("segsdgsdgds")
        }
        Timber.e(TestChipActivity::class.java.simpleName , DeviceUtils.getABIs()+" "+DeviceUtils.getMacAddress())
    }
}