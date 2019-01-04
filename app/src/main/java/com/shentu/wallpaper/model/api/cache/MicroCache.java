package com.shentu.wallpaper.model.api.cache;


import com.shentu.wallpaper.model.entity.BasePageResponse;
import com.shentu.wallpaper.model.entity.Wallpaper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.LifeCache;
import io.rx_cache2.ProviderKey;
import io.rx_cache2.Reply;

public interface MicroCache {
//    @ProviderKey("splash-ad-one-day")
//    @LifeCache(duration = 1 ,timeUnit = TimeUnit.DAYS)
//    Observable<Reply<SplashAd>> getSplashAd(Observable<SplashAd> ad, EvictProvider evictProvider);
//
//    @ProviderKey("home-category-one-day")
//    @LifeCache(duration = 1,timeUnit = TimeUnit.DAYS)
//    Observable<Reply<CategorysEntity>> getCategorys(Observable<CategorysEntity> observable, EvictProvider evictProvider);
//
    @ProviderKey("subject-detail-one-hour")
    @LifeCache(duration = 1 , timeUnit = TimeUnit.HOURS)
    Observable<Reply<BasePageResponse<Wallpaper>>> getWallPapersBySubjectId(Observable<BasePageResponse<List<Wallpaper>>> ob, DynamicKey key, EvictDynamicKey dynamicKey);
//
//    @ProviderKey("category-list-three-hour")
//    @LifeCache(duration = 3,timeUnit = TimeUnit.HOURS)
//    Observable<Reply<CategoryListEntity>> getCategoryList(Observable<CategoryListEntity> ob , DynamicKeyGroup filter, EvictDynamicKeyGroup evict);
}
