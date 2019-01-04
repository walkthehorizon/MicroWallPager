package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.mvp.contract.CategoryContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
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

//    fun getCategorys() {
//        mModel.getCategorys()
//                .compose(RxUtils.applySchedulers(mRootView))
//                .subscribe(object :ErrorHandleSubscriber<BasePageResponse<Category>>(mErrorHandler){
//                    override fun onNext(t: BasePageResponse<Category>) {
//                        mRootView.showCategorys(t.results as MutableList<Category>?)
//                    }
//                })
//    }

}

