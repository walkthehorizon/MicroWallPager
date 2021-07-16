package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.shentu.paper.app.page.BasePageModel
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.response.SubjectPageResponse
import com.shentu.paper.mvp.contract.SearchContract
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.Observable
import javax.inject.Inject


@ActivityScoped
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
