package com.shentu.paper.mvp.presenter

import android.app.Application


import com.jess.arms.mvp.BasePresenter
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.mvp.contract.MainContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject


open class MainPresenter @Inject
constructor(model: MainContract.Model, rootView: MainContract.View) : BasePresenter<MainContract.Model, MainContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler

    @Inject
    lateinit var mApplication: Application



    fun getAccountInfo() {
        mModel.loginAccount()
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<MicroUser>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<MicroUser>) {
                        if (!t.isSuccess) {
                            mRootView.showError()
                            return
                        }
                        HkUserManager.user = t.data!!
                        HkUserManager.save()
//                            Timber.d(HkUserManager.user.toString())
                        mRootView.showMainView()
                    }

                    override fun onError(t: Throwable) {
                        mRootView.showError()
                    }
                })
    }
}
