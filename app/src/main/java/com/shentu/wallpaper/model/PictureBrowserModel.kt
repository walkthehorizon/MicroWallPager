package com.shentu.wallpaper.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.BasePageResponse
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import javax.inject.Inject


@FragmentScope
class PictureBrowserModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), PictureBrowserContract.Model {

    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application;

    override fun getWallPapersBySubjectId(id: Int): Observable<BasePageResponse<Wallpaper>> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getWallpapersBySubjectId(id, 100, 0))
                .flatMap<BasePageResponse<Wallpaper>> { observable ->
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .getWallPapersBySubjectId(observable, DynamicKey(id), EvictDynamicKey(false))
                            .map { reply -> reply.data }
                }

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
