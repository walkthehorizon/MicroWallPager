package com.shentu.wallpaper.mvp.presenter

import android.app.Application

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import com.shentu.wallpaper.mvp.contract.SettingMoreContract


@ActivityScope
class SettingMorePresenter
@Inject
constructor(model: SettingMoreContract.Model, rootView: SettingMoreContract.View) :
        BasePresenter<SettingMoreContract.Model, SettingMoreContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager


    override fun onDestroy() {
        super.onDestroy();
    }
}
