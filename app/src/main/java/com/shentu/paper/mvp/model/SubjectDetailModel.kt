package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.paper.app.BasePageModel
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.response.SubjectDetailResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.SubjectDetailContract
import io.reactivex.Observable
import javax.inject.Inject


@ActivityScope
class SubjectDetailModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), SubjectDetailContract.Model {

    override fun getSubjectWallpapers(subjectId: Int, clear: Boolean): Observable<WallpaperPageResponse> {
        val offset = getOffset(clear)
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getSubjectWallpapers(subjectId, MicroService.PAGE_LIMIT, offset)
    }

    override fun getSubjectDetail(pk: Int): Observable<SubjectDetailResponse> {
        return mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getSubjectDetail(pk)
    }

    override fun getBannerWallpapers(id: Int): Observable<WallpaperPageResponse> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getBannerWallpapers(id, 100, 0)
    }

    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application;

    override fun onDestroy() {
        super.onDestroy();
    }
}
