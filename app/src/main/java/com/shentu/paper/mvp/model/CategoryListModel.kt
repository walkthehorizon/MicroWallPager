package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.paper.app.BasePageModel
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.CategoryDetailContract
import io.reactivex.Observable
import javax.inject.Inject


@FragmentScope
class CategoryListModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), CategoryDetailContract.Model {

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    init {
        limit = 30
    }

    override fun getCategoryWallpapers(id: Int, clear: Boolean): Observable<WallpaperPageResponse> {
        offset = getOffset(clear)
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getCategoryWallpapers(id, limit, offset)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}