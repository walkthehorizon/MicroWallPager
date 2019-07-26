package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.ActivityScope

import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.utils.CosUtils
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.MicroUser
import com.shentu.wallpaper.mvp.contract.MyEditContract
import com.tencent.cos.xml.exception.CosXmlClientException
import com.tencent.cos.xml.exception.CosXmlServiceException
import com.tencent.cos.xml.listener.CosXmlResultListener
import com.tencent.cos.xml.model.CosXmlRequest
import com.tencent.cos.xml.model.CosXmlResult
import com.tencent.cos.xml.model.`object`.PutObjectResult
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import javax.inject.Inject


@ActivityScope
class MyEditPresenter
@Inject
constructor(model: MyEditContract.Model, rootView: MyEditContract.View) :
        BasePresenter<MyEditContract.Model, MyEditContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mAppManager: AppManager

    fun uploadAvatar(path: String) {
        mRootView.showLoading()
        CosUtils.instance.uploadAvatar(path, object : CosXmlResultListener {
            override fun onSuccess(request: CosXmlRequest?, result: CosXmlResult?) {
                val putObjectResult = result as PutObjectResult
                HkUserManager.getInstance().user.avatar = putObjectResult.accessUrl
                updateUser()
            }

            override fun onFail(request: CosXmlRequest?, exception: CosXmlClientException?, serviceException: CosXmlServiceException?) {
                exception.let {
                    Timber.e(exception)
                }
                serviceException.let {
                    Timber.e(serviceException)
                }
                mRootView.hideLoading()
            }

        })
    }

    fun updateUser() {
        mModel.updateUser(HkUserManager.getInstance().user)
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .doOnSubscribe { mRootView.showLoading() }
                .doFinally { mRootView.hideLoading() }
                .subscribe(object : ErrorHandleSubscriber<MicroUser>(mErrorHandler) {
                    override fun onNext(t: MicroUser) {
                        mRootView.refreshView()
                        HkUserManager.getInstance().save()
                    }
                })
    }
}
