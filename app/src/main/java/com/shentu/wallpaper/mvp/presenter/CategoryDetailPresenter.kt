package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.mvp.contract.CategoryDetailContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject


@FragmentScope
class CategoryDetailPresenter
@Inject
constructor(model: CategoryDetailContract.Model, rootView: CategoryDetailContract.View) :
        BasePresenter<CategoryDetailContract.Model, CategoryDetailContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    fun getCategoryList(id: Int, page: Int, clear: Boolean) {
//        mModel.getCategoryList(id, page, clear)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe { mRootView.showLoading() }
//                .doFinally { mRootView.hideLoading() }
//                .compose(RxLifecycleUtils.bindToLifecycle<CategoryListEntity>(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<CategoryListEntity>(mErrorHandler) {
//                    override fun onNext(t: CategoryListEntity) {
//                        mRootView.showCategoryList(t.content)
//                    }
//                })
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}
