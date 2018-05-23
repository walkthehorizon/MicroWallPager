package com.shentu.wallpager.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.shentu.wallpager.mvp.contract.SplashContract;
import com.shentu.wallpager.mvp.model.api.cache.CommonCache;
import com.shentu.wallpager.mvp.model.api.service.CommonService;
import com.shentu.wallpager.mvp.model.entity.SplashAd;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.Reply;
import timber.log.Timber;


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
    public Observable<SplashAd> getSplashAd() {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(CommonService.class)
                .getSplashAd())
                .flatMap(new Function<Observable<SplashAd>, ObservableSource<SplashAd>>() {
                    @Override
                    public ObservableSource<SplashAd> apply(Observable<SplashAd> splashAdObservable) throws Exception {
                        return mRepositoryManager.obtainCacheService(CommonCache.class)
                                .getSplashAd(splashAdObservable, new EvictProvider(false))
                                .map(new Function<Reply<SplashAd>, SplashAd>() {
                                    @Override
                                    public SplashAd apply(Reply<SplashAd> splashAdReply) throws Exception {
                                        Timber.e(splashAdReply.toString());
                                        return splashAdReply.getData();
                                    }
                                });
                    }
                });
    }
}