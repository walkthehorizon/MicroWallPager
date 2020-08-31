package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.paper.app.BasePageModel
import com.shentu.paper.model.api.service.CollectService
import com.shentu.paper.model.body.DelCollectBody
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.MyCollectContract
import io.reactivex.Observable
import javax.inject.Inject


@ActivityScope
class MyCollectModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), MyCollectContract.Model {

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun getMyCollects(clear: Boolean): Observable<WallpaperPageResponse> {
        return mRepositoryManager.obtainRetrofitService(CollectService::class.java)
                .getMyCollects( getOffset(clear))
    }

    override fun delCollects(body: DelCollectBody): Observable<BaseResponse<Boolean>> {
        return mRepositoryManager.obtainRetrofitService(CollectService::class.java)
                .delCollects(body)
    }
}
