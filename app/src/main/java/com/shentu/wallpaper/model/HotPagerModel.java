package com.shentu.wallpaper.model;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.shentu.wallpaper.app.BasePageModel;
import com.shentu.wallpaper.app.Constant;
import com.shentu.wallpaper.model.api.cache.MicroCache;
import com.shentu.wallpaper.model.api.service.MicroService;
import com.shentu.wallpaper.model.entity.BasePageResponse;
import com.shentu.wallpaper.model.entity.BaseResponse;
import com.shentu.wallpaper.model.entity.Subject;
import com.shentu.wallpaper.model.response.BannerPageResponse;
import com.shentu.wallpaper.model.response.WallpaperPageResponse;
import com.shentu.wallpaper.mvp.contract.TabHomeContract;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.Reply;


@FragmentScope
public class HotPagerModel extends BasePageModel implements TabHomeContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public HotPagerModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @NonNull
    @Override
    public Observable<BaseResponse<BasePageResponse<Subject>>> getSubjects(int subjectType, boolean clear) {
        offset = clear ? 0 : (limit + offset);
        return mRepositoryManager
                .obtainRetrofitService(MicroService.class)
                .getSubjects(subjectType, limit, offset);
    }

    @NonNull
    @Override
    public Observable<WallpaperPageResponse> getRecommends(boolean clear, boolean isUser) {
        offset = clear ? 0 : (limit + offset);
        return Observable.just(mRepositoryManager.obtainRetrofitService(MicroService.class)
                .getRecommendWallpapers(limit, offset))
                .flatMap((Function<Observable<WallpaperPageResponse>, ObservableSource<WallpaperPageResponse>>)
                        ob -> mRepositoryManager.obtainCacheService(MicroCache.class)
                                .getRecommends(ob, new DynamicKey(offset), new EvictProvider(isUser)));
    }

    @NotNull
    @Override
    public Observable<BannerPageResponse> getBanners() {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService.class)
                .getBanners(Constant.BANNER_COUNT + 1, 0))
                .flatMap((Function<Observable<BannerPageResponse>, ObservableSource<BannerPageResponse>>)
                        ob -> mRepositoryManager.obtainCacheService(MicroCache.class)
                                .getBanners(ob)
                                .map(Reply::getData));
    }
}