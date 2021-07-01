package com.shentu.paper.mvp.presenter

import android.app.Application
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.paper.app.Constant
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.StateCode
import com.shentu.paper.app.event.LoginSuccessEvent
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.mvp.contract.LoginContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject


@ActivityScope
class LoginPresenter
@Inject
constructor(model: LoginContract.Model, rootView: LoginContract.View) :
        BasePresenter<LoginContract.Model, LoginContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application

    @Inject
    lateinit var mAppManager: AppManager

    @Inject
    lateinit var gson: Gson

    fun loginAccount(phone: String) {
        mModel.loginAccount(phone)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<MicroUser>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<MicroUser>) {
                        if (!t.isSuccess) {
                            return
                        }
                        if (t.code == StateCode.STATE_USER_NOT_EXIST) {
                            mRootView.showVerifyDialog()
                            return
                        }
                        SPUtils.getInstance().put(Constant.LAST_LOGIN_ACCOUNT, phone)
                        HkUserManager.user = t.data!!
                        Timber.e(HkUserManager.user.toString())
                        HkUserManager.save()
                        EventBus.getDefault().post(LoginSuccessEvent())
                        mRootView.killMyself()
                    }
                })
    }
}
