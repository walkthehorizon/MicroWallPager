package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.response.SplashAdResponse
import com.shentu.wallpaper.mvp.contract.SplashContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject


@FragmentScope
class SplashPresenter @Inject
constructor(model: SplashContract.Model, rootView: SplashContract.View) : BasePresenter<SplashContract.Model, SplashContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    fun getAd() {
        mModel.splashAd
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<SplashAdResponse>(mErrorHandler) {
                    override fun onNext(response: SplashAdResponse) {
                        mRootView.showSplash(response.data)
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
