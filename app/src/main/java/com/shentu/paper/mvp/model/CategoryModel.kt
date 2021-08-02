package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.micro.integration.IRepositoryManager
import com.shentu.paper.app.page.BasePageModel
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.response.CategoryPageResponse
import com.shentu.paper.mvp.contract.CategoryContract
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.Observable
import javax.inject.Inject


@FragmentScoped
class CategoryModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), CategoryContract.Model {

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun getCategories(clear: Boolean): Observable<CategoryPageResponse> {
        offset = if (clear) 0 else offset + 50
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getCategories(offset, 50)

    }
}
