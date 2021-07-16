package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.paper.model.api.service.UserService
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.mvp.contract.SettingMoreContract
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.Observable
import javax.inject.Inject


@ActivityScoped
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
