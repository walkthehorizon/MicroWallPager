package com.shentu.paper.model.api.service

import com.shentu.paper.model.body.DelCollectBody
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import io.reactivex.Observable
import retrofit2.http.*

interface CollectService {
    @Headers("Cache-Control: max-age=5")
    @GET("collect/my")
    suspend fun getMyCollects(@Query("offset") offset: Int): WallpaperPageResponse

    @POST("collect/modify/{id}")
    suspend fun modifyPaperCollect(@Path("id") id: Long): BaseResponse<Boolean>

    @POST("collect/del/collects")
    suspend fun delCollects(@Body body: DelCollectBody): BaseResponse<Boolean>
}