package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.response.BannerPageResponse
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.TabHomeContract
import io.reactivex.Observable
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject


@FragmentScope
class TabHomePresenter @Inject
constructor(model: TabHomeContract.Model, rootView: TabHomeContract.View) : BasePresenter<TabHomeContract.Model, TabHomeContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    fun getData(clear: Boolean) {
        val observable = if (clear)
            Observable.concat(
                    mModel.getBanners().compose(RxUtils.applyClearSchedulers(mRootView)),
                    mModel.getRecommends(clear).compose(RxUtils.applySchedulers(mRootView, clear)))
        else
            mModel.getRecommends(clear).compose(RxUtils.applySchedulers(mRootView, clear))
        observable.compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<Any>(mErrorHandler) {
                    override fun onNext(t: Any) {
                        when (t) {
                            is BannerPageResponse -> mRootView.showBanners(t.data!!.content)
                            is WallpaperPageResponse -> mRootView.showRecommends(t.data!!.content, clear)
                        }
                    }
                })
    }

    fun getRecommends(clear: Boolean) {
        mModel.getRecommends(clear)
                .compose(RxUtils.applySchedulers(mRootView, clear))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
                    override fun onNext(response: WallpaperPageResponse) {
                        if (!response.isSuccess) {
                            return
                        }
                        mRootView.showRecommends(response.data!!.content!!, clear)
                    }
                })
    }

    fun getBanners() {
        mModel.getBanners()
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BannerPageResponse>(mErrorHandler) {
                    override fun onNext(t: BannerPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showBanners(it) }
                    }
                })
    }
}
