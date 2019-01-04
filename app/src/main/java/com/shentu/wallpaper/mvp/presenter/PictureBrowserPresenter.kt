package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.BasePageResponse
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.tencent.bugly.proguard.t
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import javax.inject.Inject


@FragmentScope
class PictureBrowserPresenter
@Inject
constructor(model: PictureBrowserContract.Model, rootView: PictureBrowserContract.View) :
        BasePresenter<PictureBrowserContract.Model, PictureBrowserContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager


    fun getPictures(id: Int) {
        mModel.getWallPapersBySubjectId(id)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BasePageResponse<Wallpaper>>(mErrorHandler) {
                    override fun onNext(t: BasePageResponse<Wallpaper>) {
                        mRootView.showPictures(t.results)
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        Timber.e(t.message)
                    }
                })
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
