package com.shentu.paper.model.api.service

import com.shentu.paper.model.body.DelCollectBody
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import io.reactivex.Observable
import retrofit2.http.*

interface CollectService {
    @Headers("Cache-Control: max-age=5")
    @GET("collect/my")
    fun getMyCollects(@Query("offset") offset: Int): Observable<WallpaperPageResponse>

    @POST("collect/add/{pid}")
    fun addCollect(@Path("pid") pid: Int?): Observable<BaseResponse<Boolean>>

    @POST("collect/del/collects")
    fun delCollects(@Body body: DelCollectBody): Observable<BaseResponse<Boolean>>
}