package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.di.scope.ActivityScope

import com.jess.arms.integration.AppManager
import com.jess.arms.integration.EventBusManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.StateCode
import com.shentu.wallpaper.app.event.LoginSuccessEvent
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.MicroUser
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.mvp.contract.MainContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import javax.inject.Inject


@ActivityScope
open class MainPresenter @Inject
constructor(model: MainContract.Model, rootView: MainContract.View) : BasePresenter<MainContract.Model, MainContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler

    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mAppManager: AppManager

    fun loginAccount() {
        mModel.loginAccount()
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<MicroUser>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<MicroUser>) {
                        if (!t.isSuccess) {
                            mRootView.showError()
                            return
                        }
                        HkUserManager.instance.user = t.data!!
                        HkUserManager.instance.save()
                        mRootView.showMainView()
                        Timber.e(HkUserManager.instance.user.toString())
                    }

                    override fun onError(t: Throwable) {
                        mRootView.showError()
                    }
                })
    }
}
