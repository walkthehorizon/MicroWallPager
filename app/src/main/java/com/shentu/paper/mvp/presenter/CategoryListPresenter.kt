package com.shentu.paper.mvp.presenter

import android.app.Application

import com.jess.arms.mvp.BasePresenter
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.CategoryDetailContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject

class CategoryListPresenter
@Inject
constructor(model: CategoryDetailContract.Model, rootView: CategoryDetailContract.View) :
        BasePresenter<CategoryDetailContract.Model, CategoryDetailContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application



    fun getCategoryList(id: Int, clear: Boolean) {
        mModel.getCategoryWallpapers(id, clear)
                .compose(RxUtils.applySchedulers(mRootView, clear))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
                    override fun onNext(t: WallpaperPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showCategoryList(it,clear) }
                    }
                })
    }
}
