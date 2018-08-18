package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import android.content.Context
import android.text.TextUtils
import cn.smssdk.gui.RegisterPage
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.GsonBuilder
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.RxLifecycleUtils
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.EventBusTags
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.StateCode
import com.shentu.wallpaper.app.event.LoginSuccessEvent
import com.shentu.wallpaper.model.entity.ResultResponse
import com.shentu.wallpaper.model.entity.User
import com.shentu.wallpaper.mvp.contract.LoginContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import org.simple.eventbus.EventBus
import timber.log.Timber
import java.util.*
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
        if(!(checkPhone(phone) && checkPassword(password))) return
        mModel.registerAccount(phone, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle<ResultResponse>(mRootView))
                .subscribe(object : ErrorHandleSubscriber<ResultResponse>(mErrorHandler) {
                    override fun onNext(t: ResultResponse) {
                        ToastUtils.showShort(t.result)
                        if (t.state == 1 || t.state == StateCode.STATE_USER_EXIST) {
                            loginAccount(phone, password)
                        }
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        ToastUtils.showShort(t.message)
                    }
                })
    }

    fun loginAccount(phone: String, password: String) {
        if(!(checkPhone(phone) && checkPassword(password))) return
        mRootView.showLoading()
        mModel.loginAccount(phone, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle<ResultResponse>(mRootView))
                .subscribe(object : ErrorHandleSubscriber<ResultResponse>(mErrorHandler) {
                    override fun onNext(t: ResultResponse) {
                        ToastUtils.showShort(t.result)
                        mRootView.hideLoading()
                        if(t.state == StateCode.STATE_SUCCESS){
                            SPUtils.getInstance().put(Constant.LAST_LOGIN_ACCOUNT,phone)
                            HkUserManager.getInstance().user = ArmsUtils.obtainAppComponentFromContext(mApplication).gson().fromJson(t.data,User::class.java)
                            Timber.e(HkUserManager.getInstance().user.toString())
                            HkUserManager.getInstance().save()
                            EventBus.getDefault().post(LoginSuccessEvent(),EventBusTags.LOGIN_SUCCESS)
                            mRootView.killMyself()
                        }
                        if (t.state == StateCode.STATE_USER_NOT_EXIST) {
                            mRootView.showVerifyDialog()
                        }
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        ToastUtils.showShort(t.message)
                    }
                })
    }

    private fun checkPhone(phone: String) : Boolean{
        if (!RegexUtils.isMobileSimple(phone)) {
            ToastUtils.showShort("手机号输入错误")
            return false
        }
        return true
    }

    private fun checkPassword(password: String):Boolean{
        if (password.length < 6 || password.length >16) {
            ToastUtils.showShort("密码输入错误")
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
