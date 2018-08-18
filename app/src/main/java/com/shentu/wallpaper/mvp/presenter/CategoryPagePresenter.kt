package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import com.shentu.wallpaper.model.entity.CategoryListEntity
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import com.shentu.wallpaper.mvp.contract.CategoryPageContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber


@FragmentScope
class CategoryPagePresenter
@Inject
constructor(model: CategoryPageContract.Model, rootView: CategoryPageContract.View) :
        BasePresenter<CategoryPageContract.Model, CategoryPageContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Timber.e("initData000")
    }

    fun getCategoryList(id: Int, page: Int, clear: Boolean) {
        mModel.getCategoryList(id, page, clear)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mRootView.showLoading() }
                .doFinally { mRootView.hideLoading() }
                .compose(RxLifecycleUtils.bindToLifecycle<CategoryListEntity>(mRootView))
                .subscribe(object : ErrorHandleSubscriber<CategoryListEntity>(mErrorHandler) {
                    override fun onNext(t: CategoryListEntity) {
                        mRootView.showCategoryPicture(t.results)
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}
