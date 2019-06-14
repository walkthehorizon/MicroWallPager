package com.shentu.wallpaper.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.BaseResponse
import com.shentu.wallpaper.model.entity.MicroUser
import com.shentu.wallpaper.mvp.contract.LoginContract
import io.reactivex.Observable
import javax.inject.Inject


@ActivityScope
class LoginModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), LoginContract.Model {

    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application;

    override fun registerAccount(phone: String, password: String): Observable<BaseResponse<Boolean>> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getRegisterAccount(phone, password)
    }

    override fun loginAccount(phone: String): Observable<BaseResponse<MicroUser>> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .loginAccount(phone)
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}
