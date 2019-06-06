package com.shentu.wallpaper.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.response.CategoryPageResponse
import com.shentu.wallpaper.mvp.contract.CategoryContract
import io.reactivex.Observable
import javax.inject.Inject


@FragmentScope
class CategoryModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), CategoryContract.Model {

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun getCategories(): Observable<CategoryPageResponse> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .categories)
                .flatMap { t ->
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .getCategories(t)
                            .map { it.data }
                }
    }
}
