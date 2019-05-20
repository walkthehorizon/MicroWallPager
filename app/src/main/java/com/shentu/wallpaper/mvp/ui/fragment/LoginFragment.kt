package com.shentu.wallpaper.mvp.ui.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.di.component.DaggerLoginComponent
import com.shentu.wallpaper.di.module.LoginModule
import com.shentu.wallpaper.mvp.contract.LoginContract
import com.shentu.wallpaper.mvp.presenter.LoginPresenter
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.login_verify.*


class LoginFragment : BaseFragment<LoginPresenter>(), LoginContract.View {

    private var loadingDialog: MaterialDialog? = null

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerLoginComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .loginModule(LoginModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        SPUtils.getInstance().getString(Constant.LAST_LOGIN_ACCOUNT)?.let {
            etPhone.setText(it)
            etPhone.setSelection(it.length)
        }
        mb_login.setOnClickListener {
            mPresenter?.loginAccount(etPhone.text.toString(), et_password.text.toString())
//            mPresenter?.sendCode(this.context!!)
        }
        tv_see.setOnClickListener {
            killMyself()
        }
        tv_register.setOnClickListener {
            ToastUtils.showShort("功能暂不可用")
        }
        tv_forget_password.setOnClickListener {
            ToastUtils.showShort("功能暂不可用")
        }
    }

    override fun showVerifyDialog() {
        val dialog = MaterialDialog.Builder(this.context!!)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title("验证手机号")
                .customView(R.layout.login_verify, false)
                .build()
        dialog.vc_input.setOnCompleteListener { content ->
            ToastUtils.showShort(content)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun showLoading() {
        context?.let {
            loadingDialog = MaterialDialog.Builder(it)
                    .title(R.string.title_activity_login)
                    .content(R.string.tip_please_wait)
                    .progress(true, 0)
                    .show()
        }
    }

    override fun hideLoading() {
        loadingDialog?.dismiss()
    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        activity?.finish()
    }
}
