package com.shentu.paper.mvp.ui.activity

import android.os.Bundle
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ToastUtils
import com.micro.base.BaseActivity
import com.shentu.paper.R
import com.shentu.paper.app.base.BaseBindingActivity
import com.shentu.paper.databinding.ActivityChipTestBinding
import com.shentu.paper.mvp.presenter.LoginPresenter
import kotlinx.android.synthetic.main.activity_chip_test.*
import timber.log.Timber

class TestChipActivity : BaseBindingActivity<ActivityChipTestBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvHead.setOnClickListener {
            ToastUtils.showShort("segsdgsdgds")
        }
        Timber.e(TestChipActivity::class.java.simpleName , DeviceUtils.getABIs()+" "+DeviceUtils.getMacAddress())
    }
}