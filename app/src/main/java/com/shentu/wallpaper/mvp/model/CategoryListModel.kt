package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.wallpaper.app.BasePageModel
import com.shentu.wallpaper.mvp.contract.CategoryListContract
import javax.inject.Inject


@FragmentScope
class CategoryListModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), CategoryListContract.Model {


    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application

//    override fun getCategoryList(id: Int, page: Int, clear: Boolean): Observable<BaseResponse<Category>> {
//        offset = page * limit
//        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
//                .getCategoryById(id, limit, offset)
//        return Observable.just(mRepositoryManager
//                .obtainRetrofitService(MicroService::class.java)
//                .getCategoryById(id, limit, offset))
//                .flatMap<BaseResponse<Category>> { observable ->
//                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
//                            .getCategoryList(observable, DynamicKeyGroup(id, page), EvictDynamicKeyGroup(clear))
//                            .map { reply -> reply.data }
//                }
//    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
