package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.paper.app.page.BasePageModel
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.HomeRankContract
import io.reactivex.Observable
import javax.inject.Inject


@FragmentScope
class HomeRankModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), HomeRankContract.Model {
    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun getRankPapers(clear: Boolean): Observable<WallpaperPageResponse> {
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getRankPapers(MicroService.PAGE_LIMIT, getOffset(clear))
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
