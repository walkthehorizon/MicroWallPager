package com.shentu.wallpaper.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shentu.wallpaper.R;

import com.shentu.wallpaper.di.component.DaggerHotPagerComponent;
import com.shentu.wallpaper.di.module.HotPagerModule;
import com.shentu.wallpaper.model.entity.Subject;
import com.shentu.wallpaper.mvp.contract.HotPagerContract;

import com.shentu.wallpaper.mvp.presenter.HotPagerPresenter;
import com.shentu.wallpaper.mvp.ui.adapter.HotAdapter;
import com.shentu.wallpaper.mvp.ui.adapter.decoration.HotPageRvDecoration;
import com.shentu.wallpaper.mvp.ui.widget.CustomPopWindow;
import com.shentu.wallpaper.mvp.ui.widget.DefaultToolbar;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class HotPagerFragment extends BaseFragment<HotPagerPresenter> implements HotPagerContract.View, OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.rv_hot)
    RecyclerView rvHot;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    private Boolean isLoadData;
    @BindView(R.id.toolbarHot)
    DefaultToolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appBar;
    CustomPopWindow popWindow;
    private int subType = -1;//主题分类
    private SparseIntArray typeSparse;

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
        //初始化筛选项
        typeSparse = new SparseIntArray();
        typeSparse.append(R.id.mTvDefault, -1);
        typeSparse.append(R.id.mTvWallpaper, 1);
        typeSparse.append(R.id.mTvCos, 2);
        typeSparse.append(R.id.mTvGirl, 3);

        isLoadData = false;
        hotAdapter = new HotAdapter(null);
        rvHot.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHot.addItemDecoration(new HotPageRvDecoration(12));
        rvHot.setAdapter(hotAdapter);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setRightIcon(R.drawable.ic_more_horiz_white_24dp);
        toolbar.setOnClickListener(new DefaultToolbar.OnClickListenerImpl() {
            @Override
            public void onClickRightIcon() {
                showFilterPop();
            }
        });
        AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        lp.topMargin = BarUtils.getStatusBarHeight();
        toolbar.setLayoutParams(lp);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getUserVisibleHint() && !isLoadData) {
            isLoadData = true;
            refreshLayout.autoRefresh();
        }
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
        refreshLayout.finishLoadMore();
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
    public void onLoadMore(RefreshLayout refreshLayout) {
        assert mPresenter != null;
        mPresenter.getSubjects(subType, false);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        assert mPresenter != null;
        mPresenter.getSubjects(subType, true);
    }

    @Override
    public void showHotSubject(List<Subject> subjects, boolean clear) {
        if (clear) {
            hotAdapter.setNewData(subjects);
        } else {
            hotAdapter.addData(subjects);
        }
    }

    @Override
    public void showFilterPop() {
        if (popWindow == null) {
            ViewGroup contentView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.layout_popwindow_filter_hot, null);
            View.OnClickListener listener = view -> {
                if (popWindow != null) {
                    popWindow.dissmiss();
                }
                if (mPresenter == null || subType == typeSparse.get(view.getId())) {
                    return;
                }
                contentView.findViewById(R.id.mTvDefault).setSelected(false);
                contentView.findViewById(R.id.mTvWallpaper).setSelected(false);
                contentView.findViewById(R.id.mTvCos).setSelected(false);
                contentView.findViewById(R.id.mTvGirl).setSelected(false);
                view.setSelected(true);
                subType = typeSparse.get(view.getId());
                refreshLayout.autoRefresh();
            };
            contentView.findViewById(R.id.mTvDefault).setOnClickListener(listener);
            contentView.findViewById(R.id.mTvDefault).setSelected(true);
            contentView.findViewById(R.id.mTvWallpaper).setOnClickListener(listener);
            contentView.findViewById(R.id.mTvCos).setOnClickListener(listener);
            contentView.findViewById(R.id.mTvGirl).setOnClickListener(listener);
            popWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                    .setView(contentView)
                    .setAnimationStyle(R.style.QMUI_Animation_PopDownMenu_Right)
                    .create()
                    .showAsDropDown(toolbar, 0, 0, Gravity.END);
        } else {
            popWindow.showAsDropDown(toolbar, 0, 0, Gravity.END);
        }
    }
}
