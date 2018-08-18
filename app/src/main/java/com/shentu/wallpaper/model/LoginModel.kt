package com.shentu.wallpaper.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.ResultResponse
import javax.inject.Inject

import com.shentu.wallpaper.mvp.contract.LoginContract
import io.reactivex.Observable


@FragmentScope
class LoginModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), LoginContract.Model {

    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application;

    override fun registerAccount(phone: String, password: String): Observable<ResultResponse> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getRegisterAccount(phone, password)
    }

    override fun logoutAccount(): Observable<ResultResponse> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .logoutAccount
    }

    override fun loginAccount(phone: String, password: String): Observable<ResultResponse> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getLoginAccount(phone, password)
    }

    override fun onDestroy() {
        super.onDestroy();
    }
}
