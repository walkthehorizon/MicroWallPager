package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.mvp.contract.PasswordForgetContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject


@FragmentScope
class PasswordForgetPresenter
@Inject
constructor(model: PasswordForgetContract.Model, rootView: PasswordForgetContract.View) :
        BasePresenter<PasswordForgetContract.Model, PasswordForgetContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mAppManager: AppManager


    override fun onDestroy() {
        super.onDestroy();
    }
}
