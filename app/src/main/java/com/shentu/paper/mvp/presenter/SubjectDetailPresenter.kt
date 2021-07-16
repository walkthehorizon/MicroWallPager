package com.shentu.paper.mvp.presenter

import android.app.Application

import com.jess.arms.mvp.BasePresenter
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.response.SubjectDetailResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.SubjectDetailContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject


class SubjectDetailPresenter
@Inject
constructor(model: SubjectDetailContract.Model, rootView: SubjectDetailContract.View) :
        BasePresenter<SubjectDetailContract.Model, SubjectDetailContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application



    fun getSubjectWallpapers(id: Int, clear: Boolean) {
        mModel.getSubjectWallpapers(id, clear)
                .compose(RxUtils.applySchedulers(mRootView, clear))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
                    override fun onNext(t: WallpaperPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showWallpapers(it, clear) }
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
                        t.data?.content?.let { mRootView.showWallpapers(it, true) }
                    }
                })
    }
}
