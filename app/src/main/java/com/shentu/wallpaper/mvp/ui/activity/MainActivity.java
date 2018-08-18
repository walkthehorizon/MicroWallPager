package com.shentu.wallpaper.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.app.utils.HkUtils;
import com.shentu.wallpaper.di.component.DaggerMainComponent;
import com.shentu.wallpaper.di.module.MainModule;
import com.shentu.wallpaper.mvp.contract.MainContract;
import com.shentu.wallpaper.mvp.presenter.MainPresenter;
import com.shentu.wallpaper.mvp.ui.adapter.MainPagerAdapter;
import com.shentu.wallpaper.mvp.ui.widget.CustomPopWindow;
import com.shentu.wallpaper.mvp.ui.widget.DefaultToolbar;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View
        , ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener
        , BottomNavigationView.OnNavigationItemReselectedListener {

    @Inject
    MainPagerAdapter mainPagerAdapter;
    @Inject
    SparseIntArray itemIds;

    @BindView(R.id.main_viewpager)
    ViewPager viewPager;

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
    private int lastPos;//上一个位置

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        BarUtils.setStatusBarAlpha(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        viewPager.setOffscreenPageLimit(mainPagerAdapter.getCount());
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(mainPagerAdapter);
//        HkUtils.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == lastPos) {
            return;
        }
        if (position < 0 || position >= itemIds.size()) {
            return;
        }
        lastPos = position;
        bottomNavigationView.setSelectedItemId(itemIds.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int pos = itemIds.indexOfValue(item.getItemId());
        if (pos == -1 || pos == lastPos) {
            return false;
        }
        viewPager.setCurrentItem(pos);
        lastPos = pos;
        return true;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        Timber.e("reselected: %s", item.getTitle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        itemIds = null;
        mainPagerAdapter = null;
    }
}