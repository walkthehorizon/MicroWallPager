package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.mvp.contract.SubjectDetailContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject


@ActivityScope
class SubjectDetailPresenter
@Inject
constructor(model: SubjectDetailContract.Model, rootView: SubjectDetailContract.View) :
        BasePresenter<SubjectDetailContract.Model, SubjectDetailContract.View>(model, rootView) {
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
