package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.micro.integration.IRepositoryManager
import com.micro.mvp.BaseModel
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.model.api.service.UserService
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.mvp.contract.MainContract
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.Observable
import javax.inject.Inject
@ActivityScoped
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