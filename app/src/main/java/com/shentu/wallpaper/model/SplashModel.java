package com.shentu.wallpaper.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.shentu.wallpaper.model.api.cache.MicroCache;
import com.shentu.wallpaper.model.api.service.MicroService;
import com.shentu.wallpaper.model.response.SplashAdResponse;
import com.shentu.wallpaper.mvp.contract.SplashContract;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.Reply;


@FragmentScope
public class SplashModel extends BaseModel implements SplashContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public SplashModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<SplashAdResponse> getSplashAd() {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService.class)
                .getSplash())
                .flatMap((Function<Observable<SplashAdResponse>, ObservableSource<SplashAdResponse>>) baseResponseObservable ->
                        mRepositoryManager.obtainCacheService(MicroCache.class)
                        .getSplashAd(baseResponseObservable, new EvictProvider(false))
                        .map(Reply::getData));
    }
}