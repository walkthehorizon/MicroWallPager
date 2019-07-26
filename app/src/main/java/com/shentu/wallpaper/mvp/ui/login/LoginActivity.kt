package com.shentu.wallpaper.mvp.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.RxLifecycleUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.di.component.DaggerLoginComponent
import com.shentu.wallpaper.di.module.LoginModule
import com.shentu.wallpaper.mvp.contract.LoginContract
import com.shentu.wallpaper.mvp.presenter.LoginPresenter
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.login_verify.*
import java.util.concurrent.TimeUnit

@Route(path = "/activity/login/account")
class LoginActivity : BaseActivity<LoginPresenter>(), LoginContract.View {
    companion object {
        fun open() {
            ARouter.getInstance()
                    .build("/activity/login/account")
                    .navigation()
        }
    }

    private var loadingDialog: MaterialDialog? = null

    private var eh: EventHandler = object : EventHandler() {

        override fun afterEvent(event: Int, result: Int, data: Any) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                when (event) {
                    SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE -> {
                        mPresenter?.loginAccount(etPhone.text.toString())
                    }
                    SMSSDK.EVENT_GET_VERIFICATION_CODE -> {
                        sendCodeSuccess()
                    }
                }
            } else {
                (data as Throwable).message?.let { showMessage(it) }
            }
        }
    }

    override fun setupActivityComponent(appComponent: AppComponent) {
        BarUtils.setStatusBarAlpha(this)
        DaggerLoginComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .loginModule(LoginModule(this))
                .build()
                .inject(this)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return com.shentu.wallpaper.R.layout.fragment_login
    }

    override fun initData(savedInstanceState: Bundle?) {
        SPUtils.getInstance().getString(Constant.LAST_LOGIN_ACCOUNT)?.let {
            etPhone.setText(it)
            etPhone.setSelection(it.length)
        }
        mb_login.setOnClickListener {
            if (!RegexUtils.isMobileSimple(etPhone.text)) {
                showMessage("手机号错误！")
                return@setOnClickListener
            }
            mPresenter?.loginAccount(etPhone.text.toString())
//            if (etCode.text.length != 4) {
//                showMessage("无效的验证码！")
//                return@setOnClickListener
//            }
//            SMSSDK.submitVerificationCode("86",etPhone.text.toString(),etCode.text.toString())
        }
        tvSee.setOnClickListener {
            killMyself()
        }
        tvSendCode.setOnClickListener {
            if (!RegexUtils.isMobileSimple(etPhone.text)) {
                showMessage("手机号错误！")
                return@setOnClickListener
            }
            SMSSDK.getVerificationCode("86", etPhone.text.toString())
        }
        SMSSDK.registerEventHandler(eh)
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    fun sendCodeSuccess() {
        showMessage("验证码已发送")
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(RxLifecycleUtils.bindUntilEvent<Long, ActivityEvent>(this, ActivityEvent.DESTROY))
                .take(60)
                .map { aLong -> 59 - aLong }
                .doOnSubscribe { tvSendCode.isEnabled = false }
                .doOnComplete {
                    tvSendCode.isEnabled = true
                    tvSendCode.text = "发送验证码"
                }
                .subscribe { aLong -> tvSendCode.text = "${aLong}s" }
    }

    override fun showVerifyDialog() {
        val dialog = MaterialDialog(this)
                .cancelable(false)
                .title(text = "验证手机号")
                .customView(R.layout.login_verify, scrollable = false)
        dialog.vc_input.setOnCompleteListener { content ->
            ToastUtils.showShort(content)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun showLoading() {
        loadingDialog = MaterialDialog(this)
                .title(com.shentu.wallpaper.R.string.title_activity_login)
                .message(com.shentu.wallpaper.R.string.tip_please_wait)
        loadingDialog?.show()
    }

    override fun hideLoading() {
        loadingDialog?.dismiss()
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }
}