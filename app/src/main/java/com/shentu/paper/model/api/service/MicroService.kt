package com.shentu.paper.model.api.service

import com.shentu.paper.model.entity.AppUpdate
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.*
import io.reactivex.Observable
import retrofit2.http.*

interface MicroService {
    @get:GET("splash")
    val splash: Observable<SplashAdResponse>

    @GET("update")
    suspend fun updateInfo(): BaseResponse<AppUpdate>


    @GET("wallpapers")
    suspend fun getBannerWallpapers(
        @Query("banner_id") bannerId: Int,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = MicroService.PAGE_START
    ): WallpaperPageResponse

    @GET("wallpapers")
    fun getCategoryWallpapers(
        @Query("category_id") categoryId: Int,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Observable<WallpaperPageResponse>

    @GET("wallpapers")
    suspend fun getCategoryPapers(
        @Query("category_id") categoryId: Int,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Observable<WallpaperPageResponse>

    /**
     * 获取推荐的WallPaper
     */
    @Headers("Cache-Control: max-age=0")
    @GET("recommend")
    suspend fun getRecommendWallpapers(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = PAGE_LIMIT
    ): WallpaperPageResponse

    /**
     * 获取最新papers
     */
    @GET("paper/newest")
    fun getNewestPapers(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Observable<WallpaperPageResponse>

    /**
     * 获取排行
     * */
    @GET("paper/ranks")
    fun getRankPapers(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Observable<WallpaperPageResponse>

    @GET("categories")
    fun getCategories(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Observable<CategoryPageResponse>

    /**
     * 搜索subject
     * */
    @GET("subject/search")
    suspend fun searchSubject(
        @Query("search") key: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = PAGE_LIMIT,
    ): SubjectPageResponse

    @GET("banners")
    suspend fun getBanners(): BannerPageResponse

    @GET("banners")
    fun getBanners1(): Observable<BannerPageResponse>

    @FormUrlEncoded
    @POST("category/update/")
    fun updateCategoryCover(
        @Field("cid") cid: Int,
        @Field("logo") logo: String
    ): Observable<BaseResponse<Boolean>>

    @FormUrlEncoded
    @POST("paper/set/banner")
    fun addPaper2Banner(
        @Field("bid") bid: Int,
        @Field("pid") pid: Long
    ): Observable<BaseResponse<Boolean>>

    @PATCH("user/update/{pk}")
    fun updateUser(@Body user: MicroUser, @Path("pk") pk: Int): Observable<MicroUser>

    @POST("buy/paper/{pk}")
    fun buyPaper(
        @Path("pk") pk: Long,
        @Query("type") type: Int
    ): Observable<BaseResponse<Int>>

    @GET("wallpaper/detail/{pk}")
    fun getPaperDetail(@Path("pk") pk: Long): Observable<BaseResponse<Wallpaper>> //    /**

    /**
     * 更新分享次数
     * */
    @FormUrlEncoded
    @POST("paper/share/num")
    fun updatePaperShareNum(@Field("pid") pid: Long)

    companion object {
        const val PAGE_LIMIT = 20
        const val PAGE_START = 0
    }
}