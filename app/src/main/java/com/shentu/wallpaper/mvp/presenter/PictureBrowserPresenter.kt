package com.shentu.wallpaper.mvp.presenter

import android.app.Application

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.shentu.wallpaper.model.entity.SubjectDetailEntity
import com.shentu.wallpaper.model.entity.Wallpaper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber


@FragmentScope
class PictureBrowserPresenter
@Inject
constructor(model: PictureBrowserContract.Model, rootView: PictureBrowserContract.View) :
        BasePresenter<PictureBrowserContract.Model, PictureBrowserContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager


    fun getPictures(id: Int) {
        mModel.getAllSubjectPictures(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mRootView.showLoading() }
                .doFinally { mRootView.hideLoading() }
                .compose(RxLifecycleUtils.bindToLifecycle<SubjectDetailEntity>(mRootView))
                .subscribe(object : ErrorHandleSubscriber<SubjectDetailEntity>(mErrorHandler) {
                    override fun onNext(t: SubjectDetailEntity) {
                        print(t.toString())
                        mRootView.showPictures(t.results as MutableList<Wallpaper>)
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        Timber.e(t.message)
                    }
                })
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}
