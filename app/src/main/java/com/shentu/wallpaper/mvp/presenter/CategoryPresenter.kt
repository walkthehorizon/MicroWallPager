package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.utils.RxLifecycleUtils
import com.shentu.wallpaper.model.entity.Category
import com.shentu.wallpaper.mvp.contract.CategoryContract
import com.shentu.wallpaper.model.entity.CategorysEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import javax.inject.Inject


@FragmentScope
class CategoryPresenter
@Inject
constructor(model: CategoryContract.Model, rootView: CategoryContract.View) :
        BasePresenter<CategoryContract.Model, CategoryContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getCategorys() {
        mModel.getCategorys()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mRootView.showLoading() }
                .doFinally { mRootView.hideLoading() }
                .compose(RxLifecycleUtils.bindToLifecycle<CategorysEntity>(mRootView))
                .subscribe(object : ErrorHandleSubscriber<CategorysEntity>(mErrorHandler) {
                    override fun onNext(t: CategorysEntity) {
                        print(t.toString())
                        mRootView.showCategorys(t.results as MutableList<Category>?)
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        Timber.e(t.message)
                    }
                })
    }

}

