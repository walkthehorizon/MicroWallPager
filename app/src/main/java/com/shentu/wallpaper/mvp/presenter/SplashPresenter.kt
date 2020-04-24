package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.blankj.utilcode.util.SPUtils
import com.jess.arms.di.scope.FragmentScope

import com.jess.arms.integration.AppManager
import com.jess.arms.integration.EventBusManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.StateCode
import com.shentu.wallpaper.app.event.LoginSuccessEvent
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.MicroUser
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.model.response.SplashAdResponse
import com.shentu.wallpaper.mvp.contract.SplashContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import javax.inject.Inject


@FragmentScope
class SplashPresenter @Inject
constructor(model: SplashContract.Model, rootView: SplashContract.View) : BasePresenter<SplashContract.Model, SplashContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mAppManager: AppManager

    override fun onDestroy() {
        super.onDestroy()
    }
}
