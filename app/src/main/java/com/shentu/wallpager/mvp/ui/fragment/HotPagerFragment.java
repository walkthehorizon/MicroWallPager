package com.shentu.wallpager.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shentu.wallpager.R;
import com.shentu.wallpager.di.component.DaggerHotPagerComponent;
import com.shentu.wallpager.di.module.HotPagerModule;
import com.shentu.wallpager.mvp.contract.HotPagerContract;
import com.shentu.wallpager.mvp.model.entity.WallPager;
import com.shentu.wallpager.mvp.presenter.HotPagerPresenter;
import com.shentu.wallpager.mvp.ui.adapter.HotAdapter;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class HotPagerFragment extends BaseFragment<HotPagerPresenter> implements HotPagerContract.View, OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.rv_hot)
    RecyclerView rvHot;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    private HotAdapter hotAdapter;

    public static HotPagerFragment newInstance() {
        HotPagerFragment fragment = new HotPagerFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerHotPagerComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .hotPagerModule(new HotPagerModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot_pager, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        hotAdapter = new HotAdapter(null);
        rvHot.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvHot.setAdapter(hotAdapter);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.autoRefresh();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        refreshLayout.finishRefresh();
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

    }

    @Override
    public void showHotPager(List<WallPager> wallPagers) {
        hotAdapter.setNewData(wallPagers);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        Objects.requireNonNull(mPresenter).getWallPages();
    }
}
