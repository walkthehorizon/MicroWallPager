package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.wallpaper.app.BasePageModel
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.MyCollectContract
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
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getMyCollects(getOffset(clear))
    }
}
