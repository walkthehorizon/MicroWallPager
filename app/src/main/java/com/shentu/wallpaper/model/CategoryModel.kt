package com.shentu.wallpaper.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.mvp.contract.CategoryContract
import com.shentu.wallpaper.model.api.cache.CommonCache
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.CategorysEntity
import io.reactivex.Observable
import io.rx_cache2.EvictProvider
import javax.inject.Inject


@FragmentScope
class CategoryModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), CategoryContract.Model {


    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun getCategorys(): Observable<CategorysEntity?> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .categorys)
                .flatMap<CategorysEntity> { observable ->
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .getCategorys(observable, EvictProvider(false))
                            .map { reply -> reply.data }
                }
    }
}
