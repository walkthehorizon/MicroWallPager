package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.integration.EventBusManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.StateCode
import com.shentu.wallpaper.app.event.LoginSuccessEvent
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.BaseResponse
import com.shentu.wallpaper.model.entity.User
import com.shentu.wallpaper.mvp.contract.LoginContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import javax.inject.Inject


@FragmentScope
class LoginPresenter
@Inject
constructor(model: LoginContract.Model, rootView: LoginContract.View) :
        BasePresenter<LoginContract.Model, LoginContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    @Inject
    lateinit var gson: Gson

//    fun sendCode(context: Context) {
//        val page = RegisterPage()
//        //如果使用我们的ui，没有申请模板编号的情况下需传null
//        page.setTempCode(null)
//        page.setRegisterCallback(object : EventHandler() {
//            override fun afterEvent(event: Int, result: Int, data: Any) {
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // 处理成功的结果
//                    val phoneMap = data as HashMap<String, Any>
//                    val country = phoneMap["country"] as String // 国家代码，如“86”
//                    val phone = phoneMap["phone"] as String // 手机号码，如“13800138000”
//                    // TODO 利用国家代码和手机号码进行后续的操作
//                } else {
//                    // TODO 处理错误的结果
//                }
//            }
//        })
//        page.show(context)
//    }

    fun registerAccount(phone: String, password: String) {
        if (!(checkPhone(phone) && checkPassword(password))) return
        mModel.registerAccount(phone, password)
                .compose(RxUtils.applySchedulers(mRootView))
                .doOnSubscribe { mRootView.showLoading() }
                .doOnError { mRootView.hideLoading() }
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Boolean>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<Boolean>) {
                        ToastUtils.showShort(t.msg)
                        if (!(t.isSuccess || t.code == StateCode.STATE_USER_EXIST)) {
                            mRootView.hideLoading()
                            return
                        }
                        loginAccount(phone, password)
                    }
                })
    }

    fun loginAccount(phone: String, password: String) {
        if (!(checkPhone(phone) && checkPassword(password))) return
        mModel.loginAccount(phone, password)
                .compose(RxUtils.applySchedulers(mRootView))
                .doOnSubscribe { mRootView.showLoading() }
                .doFinally { mRootView.hideLoading() }
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<User>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<User>) {
                        if (!t.isSuccess) {
                            return
                        }
                        if (t.code == StateCode.STATE_USER_NOT_EXIST) {
                            mRootView.showVerifyDialog()
                            return
                        }
                        SPUtils.getInstance().put(Constant.LAST_LOGIN_ACCOUNT, phone)
                        HkUserManager.getInstance().user = t.data
                        Timber.e(HkUserManager.getInstance().user.toString())
                        HkUserManager.getInstance().save()
                        EventBusManager.getInstance().post(LoginSuccessEvent())
                        mRootView.killMyself()
                    }
                })
    }

    private fun checkPhone(phone: String): Boolean {
        if (!RegexUtils.isMobileSimple(phone)) {
            ToastUtils.showShort("手机号输入错误")
            return false
        }
        return true
    }

    private fun checkPassword(password: String): Boolean {
        if (password.length < 6 || password.length > 16) {
            ToastUtils.showShort("密码输入错误")
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
