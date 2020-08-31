package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.paper.app.BasePageModel
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.response.BannerPageResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.TabHomeContract
import io.reactivex.Observable
import javax.inject.Inject


@FragmentScope
class HotPagerModel @Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), TabHomeContract.Model {

    @Inject
    lateinit var mGson: Gson

    @Inject
    lateinit var mApplication: Application

    override fun getRecommends(clear: Boolean): Observable<WallpaperPageResponse> {
        offset = if (clear) 0 else limit + offset
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getRecommendWallpapers(limit, offset)
    }

    override fun getBanners(): Observable<BannerPageResponse> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getBanners()

    }
}