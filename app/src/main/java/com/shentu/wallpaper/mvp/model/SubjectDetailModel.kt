package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.wallpaper.app.BasePageModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.response.SubjectDetailResponse
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.SubjectDetailContract
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import javax.inject.Inject


@ActivityScope
class SubjectDetailModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), SubjectDetailContract.Model {

    override fun getSubjectWallpapers(subjectId: Int): Observable<WallpaperPageResponse> {
        return Observable.just(mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getSubjectWallpapers(subjectId, 100, 0))
                .flatMap {
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .getSubjectWallpapers(it, DynamicKey(subjectId))
                }
    }

    override fun getSubjectDetail(pk: Int?): Observable<SubjectDetailResponse> {
        return Observable.just(mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getSubjectDetail(pk))
                .flatMap {
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .getSubjectDetail(it, DynamicKey(pk))
                }
    }

    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application;

    override fun onDestroy() {
        super.onDestroy();
    }
}
