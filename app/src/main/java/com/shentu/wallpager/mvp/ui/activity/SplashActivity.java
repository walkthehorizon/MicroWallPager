package com.shentu.wallpager.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ScreenUtils;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.shentu.wallpager.mvp.ui.fragment.SplashFragment;
import com.tbruyelle.rxpermissions2.RxPermissions;

import com.shentu.wallpager.R;
import timber.log.Timber;

public class SplashActivity extends BaseActivity {

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenUtils.setFullScreen(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.app_activity_splash;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        requestPermissions();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, SplashFragment.newInstance(), SplashFragment.class.getSimpleName())
                .commit();
    }

    private void requestPermissions() {
        RxPermissions rxPermissions;
        rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        Timber.e(permission.name + "被允许");
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        Timber.e(permission.name + "被拒绝，可重新请求");
                    } else {
                        Timber.e(permission.name + "被拒绝，不再提示");
                    }
                });
    }
}
