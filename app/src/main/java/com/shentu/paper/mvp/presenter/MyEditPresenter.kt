package com.shentu.paper.mvp.presenter

import android.app.Application

import com.jess.arms.mvp.BasePresenter
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.utils.CosUtils
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.mvp.contract.MyEditContract
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


class MyEditPresenter
@Inject
constructor(model: MyEditContract.Model, rootView: MyEditContract.View) :
        BasePresenter<MyEditContract.Model, MyEditContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application



    fun uploadAvatar(path: String) {
        mRootView.showLoading()
        CosUtils.instance.uploadAvatar(path, object : CosXmlResultListener {
            override fun onSuccess(request: CosXmlRequest?, result: CosXmlResult?) {
                val putObjectResult = result as PutObjectResult
                HkUserManager.user.avatar = putObjectResult.accessUrl
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
        mModel.updateUser(HkUserManager.user)
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .doOnSubscribe { mRootView.showLoading() }
                .doFinally { mRootView.hideLoading() }
                .subscribe(object : ErrorHandleSubscriber<MicroUser>(mErrorHandler) {
                    override fun onNext(t: MicroUser) {
                        HkUserManager.user = t
                        HkUserManager.save()
                        mRootView.refreshView()
                    }
                })
    }
}
