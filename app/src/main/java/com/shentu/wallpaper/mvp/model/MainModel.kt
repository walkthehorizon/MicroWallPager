package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.api.service.UserService
import com.shentu.wallpaper.model.entity.MicroUser
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.mvp.contract.MainContract
import io.reactivex.Observable
import javax.inject.Inject

@ActivityScope
class MainModel @Inject constructor(repositoryManager: IRepositoryManager?) : BaseModel(repositoryManager), MainContract.Model {
    @JvmField
    @Inject
    var mGson: Gson? = null

    @JvmField
    @Inject
    var mApplication: Application? = null


    override fun loginAccount(): Observable<BaseResponse<MicroUser>> {
        return mRepositoryManager
                .obtainRetrofitService(UserService::class.java)
                .loginAccount(HkUserManager.uuid)
    }

    override fun onDestroy() {
        super.onDestroy()
        mGson = null
        mApplication = null
    }
}