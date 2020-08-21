package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.wallpaper.app.BasePageModel
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.response.BannerPageResponse
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.TabHomeContract
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictProvider
import javax.inject.Inject


@FragmentScope
class HotPagerModel @Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), TabHomeContract.Model {

    @Inject
    lateinit var mGson: Gson

    @Inject
    lateinit var mApplication: Application

    override fun getRecommends(clear: Boolean): Observable<WallpaperPageResponse> {
        offset = if (clear) 0 else limit + offset
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getRecommendWallpapers(limit, offset)
    }

    override fun getBanners(): Observable<BannerPageResponse> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getBanners()

    }
}