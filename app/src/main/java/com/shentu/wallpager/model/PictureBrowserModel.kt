package com.shentu.wallpager.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.FragmentScope
import javax.inject.Inject

import com.shentu.wallpager.mvp.contract.PictureBrowserContract
import com.shentu.wallpager.mvp.model.api.cache.CommonCache
import com.shentu.wallpager.mvp.model.api.cache.MicroCache
import com.shentu.wallpager.mvp.model.api.service.MicroService
import com.shentu.wallpager.mvp.model.entity.CategorysEntity
import com.shentu.wallpager.mvp.model.entity.SubjectDetailEntity
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import io.rx_cache2.EvictProvider


@FragmentScope
class PictureBrowserModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), PictureBrowserContract.Model {

    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application;

    override fun getAllSubjectPictures(id: Int): Observable<SubjectDetailEntity> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getWallpaperBySubjectId(id, 100, 0))
                .flatMap<SubjectDetailEntity> { observable ->
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .getWallPaperBySubjectId(observable, DynamicKey(id), EvictDynamicKey(false))
                            .map { reply -> reply.data }
                }

    }

    override fun onDestroy() {
        super.onDestroy();
    }
}
