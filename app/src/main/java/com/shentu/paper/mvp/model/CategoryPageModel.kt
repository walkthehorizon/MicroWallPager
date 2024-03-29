package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.micro.integration.IRepositoryManager
import com.shentu.paper.app.page.BasePageModel
import com.shentu.paper.mvp.contract.CategoryPageContract
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject


@FragmentScoped
class CategoryPageModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), CategoryPageContract.Model {
    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

//    override fun getCategoryList(id: Int, page: Int, clear: Boolean): Observable<CategoryListEntity> {
//        offset = page * limit
//        return Observable.just(mRepositoryManager
//                .obtainRetrofitService(MicroService::class.java)
//                .getCategoryById(id, limit, offset))
//                .flatMap<CategoryListEntity> { observable ->
//                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
//                            .getCategoryList(observable, DynamicKeyGroup(id, page), EvictDynamicKeyGroup(clear))
//                            .map { reply -> reply.data }
//                }
//    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
