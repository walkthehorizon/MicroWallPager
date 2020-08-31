package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.paper.model.api.service.CollectService
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.PictureBrowserContract
import io.reactivex.Observable
import javax.inject.Inject


@ActivityScope
class PictureBrowserModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), PictureBrowserContract.Model {

    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application

    override fun addCollect(pid: Int): Observable<BaseResponse<Boolean>> {
        return mRepositoryManager.obtainRetrofitService(CollectService::class.java)
                .addCollect(pid)
    }

    override fun getWallPapersBySubjectId(id: Int): Observable<WallpaperPageResponse> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getSubjectWallpapers(id, 100, 0)
    }

    override fun updateCategoryCover(cid: Int, logo: String): Observable<BaseResponse<Boolean>> {
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .updateCategoryCover(cid, logo)
    }

    override fun buyPaper(pk: Int, pea: Int): Observable<BaseResponse<Int>> {
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .buyPaper(pk, pea)
    }

    override fun getPaperDetail(pk: Int): Observable<BaseResponse<Wallpaper>> {
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getPaperDetail(pk)
    }

    override fun addPaper2Banner(bid: Int, pid: Int): Observable<BaseResponse<Boolean>> {
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .addPaper2Banner(bid, pid)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
