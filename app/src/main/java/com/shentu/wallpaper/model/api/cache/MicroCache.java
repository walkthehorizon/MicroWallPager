package com.shentu.wallpaper.model.api.cache;


import com.shentu.wallpaper.model.entity.CategoryListEntity;
import com.shentu.wallpaper.model.entity.CategorysEntity;
import com.shentu.wallpaper.model.entity.SplashAd;
import com.shentu.wallpaper.model.entity.SubjectDetailEntity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictDynamicKeyGroup;
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

    @ProviderKey("category-list-three-hour")
    @LifeCache(duration = 3,timeUnit = TimeUnit.HOURS)
    Observable<Reply<CategoryListEntity>> getCategoryList(Observable<CategoryListEntity> ob , DynamicKeyGroup filter, EvictDynamicKeyGroup evict);
}
