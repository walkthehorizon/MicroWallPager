package com.shentu.paper.mvp.presenter

import android.app.Application
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.body.DelCollectBody
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.MyCollectContract
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
    lateinit var mAppManager: AppManager

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

    fun delCollects(body: DelCollectBody) {
        mModel.delCollects(body)
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .doOnSubscribe {
                    mRootView.showProgress()
                }
                .doFinally {
                    mRootView.hideProgress()
                }
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Boolean>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<Boolean>) {
                        if (!t.isSuccess) {
                            return
                        }
                        mRootView.closeDelMode(true)
                        ToastUtils.showShort("删除成功")
                    }
                })
    }

}
