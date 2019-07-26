package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.CategoryDetailContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject

@FragmentScope
class CategoryListPresenter
@Inject
constructor(model: CategoryDetailContract.Model, rootView: CategoryDetailContract.View) :
        BasePresenter<CategoryDetailContract.Model, CategoryDetailContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mAppManager: AppManager

    fun getCategoryList(id: Int, clear: Boolean) {
        mModel.getCategoryWallpapers(id, clear)
                .compose(RxUtils.applySchedulers(mRootView, clear))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
                    override fun onNext(t: WallpaperPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        mRootView.showCategoryList(t.data?.content as MutableList<Wallpaper>)
                    }
                })
    }
}
