package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.wallpaper.app.BasePageModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.response.SubjectPageResponse
import com.shentu.wallpaper.mvp.contract.SearchContract
import io.reactivex.Observable
import io.rx_cache2.DynamicKeyGroup
import io.rx_cache2.Reply
import javax.inject.Inject


@ActivityScope
class SearchModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), SearchContract.Model {

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun searchKey(key: String, clear: Boolean): Observable<SubjectPageResponse> {
        offset = getOffset(clear)
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .searchSubject(key, limit, offset))
                .flatMap {
                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
                            .searchKey(it, DynamicKeyGroup(key, offset))
                            .map { t: Reply<SubjectPageResponse> -> t.data }
                }
    }
}
