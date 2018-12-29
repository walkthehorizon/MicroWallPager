package com.shentu.wallpaper.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.shentu.wallpaper.app.utils.RxUtils;
import com.shentu.wallpaper.model.entity.SplashAd;
import com.shentu.wallpaper.mvp.contract.SplashContract;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import timber.log.Timber;


@FragmentScope
public class SplashPresenter extends BasePresenter<SplashContract.Model, SplashContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SplashPresenter(SplashContract.Model model, SplashContract.View rootView) {
        super(model, rootView);
    }

    public void getAd() {
        mModel.getSplashAd()
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(new ErrorHandleSubscriber<SplashAd>(mErrorHandler) {
                    @Override
                    public void onNext(SplashAd splashAd) {
                        mRootView.showSplash(splashAd);
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
