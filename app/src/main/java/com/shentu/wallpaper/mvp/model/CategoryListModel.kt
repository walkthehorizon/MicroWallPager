package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.app.BasePageModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.CategoryListEntity
import javax.inject.Inject

import com.shentu.wallpaper.mvp.contract.CategoryListContract
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.DynamicKeyGroup
import io.rx_cache2.EvictDynamicKey
import io.rx_cache2.EvictDynamicKeyGroup


@FragmentScope
class CategoryListModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), CategoryListContract.Model {


    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application

    override fun getCategoryList(id: Int, page: Int, clear: Boolean): Observable<CategoryListEntity> {
        offset = page * limit
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getCategoryList(id, limit, offset))
                .flatMap<CategoryListEntity> { observable ->
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .getCategoryList(observable, DynamicKeyGroup(id, page), EvictDynamicKeyGroup(clear))
                            .map { reply -> reply.data }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
