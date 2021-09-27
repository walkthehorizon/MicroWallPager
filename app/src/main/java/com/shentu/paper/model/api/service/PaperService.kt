package com.shentu.paper.model.api.service

import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.BaseResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaperService {
    @POST("garbage/set/{paperId}")
    fun setGarbage(@Path("paperId") paperId: Long):Observable<BaseResponse<String>>

    @GET("wallpaper/detail/{pk}")
    suspend fun getPaperDetail(@Path("pk") pk: Long): BaseResponse<Wallpaper>
}