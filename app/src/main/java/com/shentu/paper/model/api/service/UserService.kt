package com.shentu.paper.model.api.service

import com.shentu.paper.app.HkUserManager
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.model.response.BaseResponse
import io.reactivex.Observable
import retrofit2.http.*

interface UserService {
    @GET("sign")
    suspend fun sign(): BaseResponse<Int>

    @Headers("Cache-Control:max-age=0")
    @GET("account/info")
    suspend fun loginAccount(@Query("uuid") uuid: String = HkUserManager.uuid): BaseResponse<MicroUser>

    @FormUrlEncoded
    @POST("account/register/")
    fun getRegisterAccount(@Field("phone") phone: String,
                           @Field("password") password: String): Observable<BaseResponse<Boolean>>

    @POST("account/logout/")
    fun logout(): Observable<BaseResponse<String>>
}