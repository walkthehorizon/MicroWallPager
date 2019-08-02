package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.ActivityScope

import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.response.SubjectDetailResponse
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.SubjectDetailContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
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
    lateinit var mAppManager: AppManager

    fun getSubjectWallpapers(id: Int) {
        mModel.getSubjectWallpapers(id)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
                    override fun onNext(t: WallpaperPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showWallpapers(it) }
                    }
                })
    }

    fun getSubjectDetail(id: Int) {
        mModel.getSubjectDetail(id)
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<SubjectDetailResponse>(mErrorHandler) {
                    override fun onNext(t: SubjectDetailResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.let { mRootView.showDetail(it) }
                    }
                })
    }

    fun getBannerWallpapers(id: Int) {
        mModel.getBannerWallpapers(id)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
                    override fun onNext(t: WallpaperPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showWallpapers(it) }
                    }
                })
    }
}
