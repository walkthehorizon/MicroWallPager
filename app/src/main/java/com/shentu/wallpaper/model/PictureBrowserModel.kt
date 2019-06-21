package com.shentu.wallpaper.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.BaseResponse
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import javax.inject.Inject


@ActivityScope
class PictureBrowserModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), PictureBrowserContract.Model {


    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application

    override fun getWallPapersBySubjectId(id: Int): Observable<WallpaperPageResponse> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getSubjectWallpapers(id, 100, 0))
                .flatMap { t ->
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .getWallPapersBySubjectId(t, DynamicKey(id), EvictDynamicKey(false))
                            .map { it.data }
                }

    }

    override fun updateCategoryCover(cid: Int?, logo: String): Observable<BaseResponse<Boolean>> {
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .updateCategoryCover(cid, logo);
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
