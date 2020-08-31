package com.shentu.paper.model.api.service

import com.shentu.paper.model.entity.Comment
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.model.response.CommentPageResponse
import io.reactivex.Observable
import retrofit2.http.*

interface CommentService {

    /**
     * 获取paper的评论
     * */
    @GET("paper/comments")
    fun getPaperComments(@Query("paper_id") paper_id: Int,
                         @Query("limit") limit: Int,
                         @Query("offset") offset: Int): Observable<CommentPageResponse>

    /**
     * 新增评论
     * */
    @FormUrlEncoded
    @POST("paper/comments/add")
    fun addPaperComments(@Field("pid") pid: Int,
                         @Field("content") content: String): Observable<BaseResponse<Comment>>
}