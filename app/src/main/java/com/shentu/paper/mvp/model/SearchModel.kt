package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.paper.app.page.BasePageModel
import com.shentu.paper.model.api.cache.MicroCache
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.response.SubjectPageResponse
import com.shentu.paper.mvp.contract.SearchContract
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
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .searchSubject(key, limit, offset)
    }
}
