package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.wallpaper.app.BasePageModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.CategoryDetailContract
import io.reactivex.Observable
import io.rx_cache2.DynamicKeyGroup
import javax.inject.Inject


@FragmentScope
class CategoryListModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), CategoryDetailContract.Model {


    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun getCategoryWallpapers(id: Int, clear: Boolean): Observable<WallpaperPageResponse> {
        offset = getOffset(clear)
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getCategoryWallpapers(id, limit, offset))
                .flatMap<WallpaperPageResponse> { observable ->
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .getCategoryWallpapers(observable, DynamicKeyGroup(id, offset))
                            .map { reply -> reply.data }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
