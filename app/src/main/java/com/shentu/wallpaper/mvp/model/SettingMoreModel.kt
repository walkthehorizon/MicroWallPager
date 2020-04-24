package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.api.service.UserService
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.mvp.contract.SettingMoreContract
import io.reactivex.Observable
import javax.inject.Inject


@ActivityScope
class SettingMoreModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), SettingMoreContract.Model {
    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application;

    override fun logout(): Observable<BaseResponse<String>> {
        return mRepositoryManager
                .obtainRetrofitService(UserService::class.java)
                .logout()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
