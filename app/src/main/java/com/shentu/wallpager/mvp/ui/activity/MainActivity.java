package com.shentu.wallpager.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.SparseIntArray;
import android.view.MenuItem;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.shentu.wallpager.R;
import com.shentu.wallpager.di.component.DaggerMainComponent;
import com.shentu.wallpager.di.module.MainModule;
import com.shentu.wallpager.mvp.contract.MainContract;
import com.shentu.wallpager.mvp.presenter.MainPresenter;
import com.shentu.wallpager.mvp.ui.adapter.MainPagerAdapter;
import com.shentu.wallpager.mvp.ui.widget.DefaultToolbar;

import javax.inject.Inject;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    MainPagerAdapter mainPagerAdapter;
    @Inject
    SparseIntArray itemIds;

    @BindView(R.id.main_viewpager)
    ViewPager viewPager;

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.default_toolbar)
    DefaultToolbar toolbar;

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
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        toolbar.setTitle(getResources().getString(R.string.app_name));
        viewPager.setOffscreenPageLimit(mainPagerAdapter.getCount());
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(mainPagerAdapter);
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
        if (position < 0 || position >= itemIds.size()) {
            return;
        }
        bottomNavigationView.setSelectedItemId(itemIds.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int pos = itemIds.indexOfValue(item.getItemId());
        if (pos == -1) {
            return false;
        }
        viewPager.setCurrentItem(pos);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPagerAdapter = null;
        bottomNavigationView.setOnNavigationItemSelectedListener(null);
        itemIds = null;
        mainPagerAdapter = null;
    }
}
