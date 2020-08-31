package com.shentu.paper.model.api.service

import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.model.response.BaseResponse
import io.reactivex.Observable
import retrofit2.http.*

interface UserService {
    @GET("sign")
    fun sign(): Observable<BaseResponse<Int>>

    @GET("account/info")
    fun loginAccount(@Query("uuid") phone: String): Observable<BaseResponse<MicroUser>>

    @FormUrlEncoded
    @POST("account/register/")
    fun getRegisterAccount(@Field("phone") phone: String,
                           @Field("password") password: String): Observable<BaseResponse<Boolean>>

    @POST("account/logout/")
    fun logout(): Observable<BaseResponse<String>>
}