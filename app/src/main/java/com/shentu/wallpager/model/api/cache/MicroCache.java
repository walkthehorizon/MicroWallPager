package com.shentu.wallpager.mvp.model.api.cache;

import com.shentu.wallpager.mvp.model.entity.CategorysEntity;
import com.shentu.wallpager.mvp.model.entity.SplashAd;
import com.shentu.wallpager.mvp.model.entity.SubjectDetailEntity;
import com.shentu.wallpager.mvp.model.entity.Wallpaper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;
import io.rx_cache2.ProviderKey;
import io.rx_cache2.Reply;

public interface MicroCache {
    @ProviderKey("splash-ad-one-day")
    @LifeCache(duration = 1 ,timeUnit = TimeUnit.DAYS)
    Observable<Reply<SplashAd>> getSplashAd(Observable<SplashAd> ad, EvictProvider evictProvider);

    @ProviderKey("home-category-one-day")
    @LifeCache(duration = 1,timeUnit = TimeUnit.DAYS)
    Observable<Reply<CategorysEntity>> getCategorys(Observable<CategorysEntity> observable, EvictProvider evictProvider);

    @ProviderKey("subject-detail-one-hour")
    @LifeCache(duration = 1 , timeUnit = TimeUnit.HOURS)
    Observable<Reply<SubjectDetailEntity>> getWallPaperBySubjectId(Observable<SubjectDetailEntity> ob, DynamicKey key, EvictDynamicKey dynamicKey);
}
