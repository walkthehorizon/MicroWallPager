package com.shentu.wallpaper.model.api.cache;


import com.shentu.wallpaper.model.response.BannerPageResponse;
import com.shentu.wallpaper.model.response.CategoryPageResponse;
import com.shentu.wallpaper.model.response.SplashAdResponse;
import com.shentu.wallpaper.model.response.SubjectDetailResponse;
import com.shentu.wallpaper.model.response.SubjectPageResponse;
import com.shentu.wallpaper.model.response.WallpaperPageResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;
import io.rx_cache2.ProviderKey;
import io.rx_cache2.Reply;

public interface MicroCache {
    @ProviderKey("splash-ad-one-day")
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<SplashAdResponse>> getSplashAd(Observable<SplashAdResponse> ad, EvictProvider evictProvider);

    //    @ProviderKey("home-category-one-day")
//    @LifeCache(duration = 1,timeUnit = TimeUnit.DAYS)
//    Observable<Reply<CategorysEntity>> getCategories(Observable<CategorysEntity> observable, EvictProvider evictProvider);
//
    @ProviderKey("subject-list-one-day")
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<WallpaperPageResponse>>
    getWallPapersBySubjectId(Observable<WallpaperPageResponse> ob
            , DynamicKey key, EvictDynamicKey dynamicKey);

    @ProviderKey("banner-wallpapers-one-day")
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<WallpaperPageResponse>>
    getWallPapersByBannerId(Observable<WallpaperPageResponse> ob
            , DynamicKey key);

    @ProviderKey("category-list-one-day")
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<WallpaperPageResponse>>
    getCategoryWallpapers(Observable<WallpaperPageResponse> ob,
                          DynamicKeyGroup filter);

    @ProviderKey("subject-search-third-hours")
    @LifeCache(duration = 3, timeUnit = TimeUnit.HOURS)
    Observable<Reply<SubjectPageResponse>>
    searchKey(Observable<SubjectPageResponse> ob,
              DynamicKeyGroup filter);

    @ProviderKey("banner-one-hour")
    @LifeCache(duration = 1, timeUnit = TimeUnit.HOURS)
    Observable<Reply<BannerPageResponse>>
    getBanners(Observable<BannerPageResponse> ob);

    @ProviderKey("categories-one-day")
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<CategoryPageResponse>>
    getCategories(Observable<CategoryPageResponse> ob, EvictDynamicKey evict, DynamicKey offset);

    @ProviderKey("recommends-one-month")
    @LifeCache(duration = 30, timeUnit = TimeUnit.DAYS)
    Observable<WallpaperPageResponse> getRecommends(
            Observable<WallpaperPageResponse> ob,
            DynamicKey page,
            EvictProvider evict);

    @ProviderKey("subject-wallpaper-one-day")
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<WallpaperPageResponse>
    getSubjectWallpapers(Observable<WallpaperPageResponse> observable,
                         DynamicKey subjectId);

    @ProviderKey("subject-detail-one-day")
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<SubjectDetailResponse>
    getSubjectDetail(Observable<SubjectDetailResponse> observable,
                     DynamicKey subjectId);
}
