//package com.shentu.paper.mvp.presenter
//
//import android.app.Application
//
//import com.jess.arms.mvp.BasePresenter
//import com.shentu.paper.app.utils.RxUtils
//import com.shentu.paper.model.response.BannerPageResponse
//import com.shentu.paper.model.response.BaseResponse
//import com.shentu.paper.model.response.WallpaperPageResponse
//import com.shentu.paper.mvp.contract.TabHomeContract
//import io.reactivex.Observable
//import me.jessyan.rxerrorhandler.core.RxErrorHandler
//import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
//import timber.log.Timber
//import javax.inject.Inject
//
//
//class TabHomePresenter @Inject
//constructor(model: TabHomeContract.Model, rootView: TabHomeContract.View) : BasePresenter<TabHomeContract.Model, TabHomeContract.View>(model, rootView) {
//    @Inject
//    lateinit var mErrorHandler: RxErrorHandler
//    @Inject
//    lateinit var mApplication: Application
//
//    fun getData(clear: Boolean) {
//        val observable = if (clear)
//            Observable.concat(mModel.getBanners(), mModel.getRecommends(clear))
//        else
//            mModel.getRecommends(clear)
//
//        observable.compose(RxUtils.applySchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<BaseResponse<*>>(mErrorHandler) {
//                    override fun onNext(t: BaseResponse<*>) {
//                        if (!t.isSuccess) {
//                            return
//                        }
//                        if (t is WallpaperPageResponse) {
//                            t.data?.content?.let { mRootView.showRecommends(it, clear) }
//                        }
//                        if (t is BannerPageResponse) {
//                            t.data?.content?.let { mRootView.showBanners(it) }
//                        }
//                    }
//                })
//    }
//
//    fun getRecommends(clear: Boolean) {
//        Timber.e("load recommend")
//        mModel.getRecommends(clear)
//                .compose(RxUtils.applySchedulers(mRootView, clear))
//                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
//                    override fun onNext(response: WallpaperPageResponse) {
//                        if (!response.isSuccess) {
//                            return
//                        }
//                        mRootView.showRecommends(response.data!!.content, clear)
//                    }
//                })
//    }
//
//    fun getBanners() {
//        mModel.getBanners()
//                .compose(RxUtils.applyClearSchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<BannerPageResponse>(mErrorHandler) {
//                    override fun onNext(t: BannerPageResponse) {
//                        if (!t.isSuccess) {
//                            return
//                        }
//                        t.data?.content?.let { mRootView.showBanners(it) }
//                    }
//                })
//    }
//}
