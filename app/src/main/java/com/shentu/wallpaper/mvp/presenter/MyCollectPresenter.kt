package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.BaseResponse
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.MyCollectContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject


@ActivityScope
class MyCollectPresenter
@Inject
constructor(model: MyCollectContract.Model, rootView: MyCollectContract.View) :
        BasePresenter<MyCollectContract.Model, MyCollectContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    fun addCollect(pid: Int) {
        mModel.addCollect(pid)
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Boolean>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<Boolean>) {
                        if (!t.isSuccess) {
                            return
                        }
                        mRootView.showMessage("收藏成功")
                    }
                })
    }

    fun getMyCollects(clear: Boolean) {
        mModel.getMyCollects(clear)
                .compose(RxUtils.applySchedulers(mRootView, clear))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
                    override fun onNext(t: WallpaperPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showCollects(it, clear) }
                    }
                })
    }

}
