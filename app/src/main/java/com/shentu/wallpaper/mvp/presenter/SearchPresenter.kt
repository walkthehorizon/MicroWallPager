package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.response.SubjectPageResponse
import com.shentu.wallpaper.mvp.contract.SearchContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject


@ActivityScope
class SearchPresenter
@Inject
constructor(model: SearchContract.Model, rootView: SearchContract.View) :
        BasePresenter<SearchContract.Model, SearchContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager


    fun searchKey(key: String, clear: Boolean) {
        mModel.searchKey(key, clear)
                .compose(RxUtils.applySchedulers(mRootView, clear))
                .subscribe(object : ErrorHandleSubscriber<SubjectPageResponse>(mErrorHandler) {
                    override fun onNext(t: SubjectPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showResults(it, clear) }
                    }
                })
    }
}
