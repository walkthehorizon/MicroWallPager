package com.shentu.wallpaper.mvp.ui.login

import android.annotation.SuppressLint
import android.os.Bundle

import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.shentu.wallpaper.R
import com.shentu.wallpaper.mvp.ui.fragment.SplashFragment
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.RequestExecutor

class SplashActivity : BaseActivity<IPresenter>() {

    override fun setupActivityComponent(appComponent: AppComponent) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        ScreenUtils.setFullScreen(this)
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.app_activity_splash
    }

    override fun initData(savedInstanceState: Bundle?) {
        requestPermissions()
    }

    //    @SuppressLint("CheckResult")
    //    private void requestPermissions() {
    //        RxPermissions rxPermissions;
    //        rxPermissions = new RxPermissions(this);
    //        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    //                .subscribe(permission -> {
    //                    if (permission.granted) {
    //                        Timber.e("%s被允许", permission.name);
    //                    } else if (permission.shouldShowRequestPermissionRationale) {
    //                        Timber.e("%s被拒绝，可重新请求", permission.name);
    //                    } else {
    //                        Timber.e("%s被拒绝，不再提示", permission.name);
    //                    }
    //                });
    //
    //    }

    private fun showSplash() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, SplashFragment.newInstance(), SplashFragment::class.java.simpleName)
                .commit()
    }

    @SuppressLint("CheckResult")
    fun requestPermissions() {
        AndPermission.with(this)
                .runtime()
                .permission(*Permission.Group.STORAGE)
                .rationale { _, _, executor -> showPermissionRequestDialog(executor) }
                .onGranted { showSplash() }
                .onDenied { ToastUtils.showShort("权限被拒绝，启动失败") }.start()
    }

    private fun showPermissionRequestDialog(executor: RequestExecutor) {
        MaterialDialog.Builder(this)
                .title("权限请求")
                .content("存储权限用于存储文件资料等，请允许授权请求")
                .positiveText("知道了")
                .cancelable(false)
                .onPositive { dialog, which ->
                    executor.execute()
                    dialog.dismiss()
                }
                .show()
    }
}
