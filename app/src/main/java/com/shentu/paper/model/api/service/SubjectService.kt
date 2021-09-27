package com.shentu.paper.model.api.service

import com.shentu.paper.model.response.SubjectDetailResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SubjectService {

    @GET("subjects/{pk}")
    suspend fun getSubjectDetail(@Path("pk") pk: Int): SubjectDetailResponse

    @GET("wallpapers")
    suspend fun getSubjectAllPapers(
        @Query("subject_id") subjectId: Int,
        @Query("limit") limit: Int = 200,
    ): WallpaperPageResponse

}