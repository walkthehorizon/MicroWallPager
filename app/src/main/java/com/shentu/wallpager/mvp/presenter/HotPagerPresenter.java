package com.shentu.wallpager.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.shentu.wallpager.mvp.contract.HotPagerContract;
import com.shentu.wallpager.mvp.model.entity.WallPager;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;


@FragmentScope
public class HotPagerPresenter extends BasePresenter<HotPagerContract.Model, HotPagerContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public HotPagerPresenter(HotPagerContract.Model model, HotPagerContract.View rootView) {
        super(model, rootView);
    }

    public void getWallPages() {
        mModel.getWallPageList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new Consumer<List<WallPager>>() {
            @Override
            public void accept(List<WallPager> wallPagers) throws Exception {
                mRootView.hideLoading();
                mRootView.showHotPager(wallPagers);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
