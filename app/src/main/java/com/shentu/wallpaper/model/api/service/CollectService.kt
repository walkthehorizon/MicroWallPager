package com.shentu.wallpaper.model.api.service

import com.shentu.wallpaper.model.body.DelCollectBody
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import io.reactivex.Observable
import retrofit2.http.*

interface CollectService {
    @GET("collect/my")
    fun getMyCollects(@Query("offset") offset: Int): Observable<WallpaperPageResponse>

    @POST("collect/add/{pid}")
    fun addCollect(@Path("pid") pid: Int?): Observable<BaseResponse<Boolean>>

    @POST("collect/del/collects")
    fun delCollects(@Body body: DelCollectBody): Observable<BaseResponse<Boolean>>
}