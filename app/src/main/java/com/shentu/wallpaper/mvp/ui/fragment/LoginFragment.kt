package com.shentu.wallpaper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils

import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils


import com.shentu.wallpaper.di.module.LoginModule
import com.shentu.wallpaper.mvp.contract.LoginContract
import com.shentu.wallpaper.mvp.presenter.LoginPresenter

import com.shentu.wallpaper.R
import com.shentu.wallpaper.R.id.*
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.di.component.DaggerLoginComponent
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.login_verify.*


/**
 * 如果没presenter
 * 你可以这样写
 *
 * @FragmentScope(請注意命名空間) class NullObjectPresenterByFragment
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class LoginFragment : BaseFragment<LoginPresenter>(), LoginContract.View {

    private var loadingDialog: MaterialDialog? = null

    companion object {
        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()
            return fragment
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
            et_phone.setText(it)
        }
        mb_login.setOnClickListener {
            mPresenter?.loginAccount(et_phone.text.toString(), et_password.text.toString())
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

    /**
     * 通过此方法可以使 Fragment 能够与外界做一些交互和通信, 比如说外部的 Activity 想让自己持有的某个 Fragment 对象执行一些方法,
     * 建议在有多个需要与外界交互的方法时, 统一传 {@link Message}, 通过 what 字段来区分不同的方法, 在 {@link #setData(Object)}
     * 方法中就可以 {@code switch} 做不同的操作, 这样就可以用统一的入口方法做多个不同的操作, 可以起到分发的作用
     * <p>
     * 调用此方法时请注意调用时 Fragment 的生命周期, 如果调用 {@link #setData(Object)} 方法时 {@link Fragment#onCreate(Bundle)} 还没执行
     * 但在 {@link #setData(Object)} 里却调用了 Presenter 的方法, 是会报空的, 因为 Dagger 注入是在 {@link Fragment#onCreate(Bundle)} 方法中执行的
     * 然后才创建的 Presenter, 如果要做一些初始化操作,可以不必让外部调用 {@link #setData(Object)}, 在 {@link #initData(Bundle)} 中初始化就可以了
     * <p>
     * Example usage:
     * <pre>
     *fun setData(data:Any) {
     *   if(data is Message){
     *       when (data.what) {
     *           0-> {
     *               //根据what 做你想做的事情
     *           }
     *           else -> {
     *           }
     *       }
     *   }
     *}
     *
     *
     *
     *
     *
     * // call setData(Object):
     * val data = Message();
     * data.what = 0;
     * data.arg1 = 1;
     * fragment.setData(data);
     * </pre>
     *
     * @param data 当不需要参数时 {@code data} 可以为 {@code null}
     */
    override fun setData(data: Any?) {

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
