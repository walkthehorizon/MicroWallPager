package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.HomeRankContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject


@FragmentScope
class HomeRankPresenter
@Inject
constructor(model: HomeRankContract.Model, rootView: HomeRankContract.View) :
        BasePresenter<HomeRankContract.Model, HomeRankContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mAppManager: AppManager

    fun getRankPapers(clear: Boolean) {
        mModel.getRankPapers(clear)
                .compose(RxUtils.applySchedulers(mRootView, clear))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
                    override fun onNext(t: WallpaperPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showRanks(it, clear) }
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}
