package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.CollectService
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.model.response.SubjectDetailResponse
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

    override fun addCollect(pid: Int): Observable<BaseResponse<Boolean>> {
        return mRepositoryManager.obtainRetrofitService(CollectService::class.java)
                .addCollect(pid)
    }

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
                .updateCategoryCover(cid, logo)
    }

    override fun buyPaper(pk: Int, pea: Int): Observable<BaseResponse<String>> {
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .buyPaper(pk, pea)
    }

    override fun getShareSubject(pk: Int?): Observable<SubjectDetailResponse> {
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getSubjectDetail(pk)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
