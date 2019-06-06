package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.di.component.DaggerSubjectDetailComponent
import com.shentu.wallpaper.di.module.SubjectDetailModule
import com.shentu.wallpaper.mvp.contract.SubjectDetailContract
import com.shentu.wallpaper.mvp.presenter.SubjectDetailPresenter

class SubjectDetailActivity : BaseActivity<SubjectDetailPresenter>(), SubjectDetailContract.View {

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerSubjectDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .subjectDetailModule(SubjectDetailModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_subject_detail //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {

    }


    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }
}
