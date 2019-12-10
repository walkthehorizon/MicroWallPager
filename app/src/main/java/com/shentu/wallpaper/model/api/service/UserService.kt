package com.shentu.wallpaper.model.api.service

import com.shentu.wallpaper.model.entity.MicroUser
import com.shentu.wallpaper.model.response.BaseResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UserService {
    @Headers(HEADER_API_VERSION)
    @GET("/users")
    fun getUsers(@Query("since") lastIdQueried: Int, @Query("per_page") perPage: Int): Observable<List<MicroUser>>

    @GET("sign")
    fun sign(): Observable<BaseResponse<Int>>

    companion object {
        const val HEADER_API_VERSION = "Accept: application/vnd.github.v3+json"
    }
}