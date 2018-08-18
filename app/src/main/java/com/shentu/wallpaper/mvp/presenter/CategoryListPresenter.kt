package com.shentu.wallpaper.mvp.presenter

import android.app.Application

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import com.shentu.wallpaper.model.entity.CategoryListEntity
import com.shentu.wallpaper.model.entity.Wallpaper
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import com.shentu.wallpaper.mvp.contract.CategoryListContract
import com.shentu.wallpaper.mvp.model.CategoryListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber


@FragmentScope
class CategoryListPresenter
@Inject
constructor(model: CategoryListContract.Model, rootView: CategoryListContract.View) :
        BasePresenter<CategoryListContract.Model, CategoryListContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    fun getCategoryList(id: Int, page: Int, clear: Boolean) {
        mModel.getCategoryList(id, page, clear)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mRootView.showLoading() }
                .doFinally { mRootView.hideLoading() }
                .compose(RxLifecycleUtils.bindToLifecycle<CategoryListEntity>(mRootView))
                .subscribe(object : ErrorHandleSubscriber<CategoryListEntity>(mErrorHandler) {
                    override fun onNext(t: CategoryListEntity) {
                        mRootView.showCategoryList(t.results)
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}
