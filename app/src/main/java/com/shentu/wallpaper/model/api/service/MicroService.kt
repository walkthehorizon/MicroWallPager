package com.shentu.wallpaper.model.api.service

import com.shentu.wallpaper.model.entity.AppUpdate
import com.shentu.wallpaper.model.entity.MicroUser
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.model.response.*
import io.reactivex.Observable
import retrofit2.http.*

interface MicroService {
    @get:GET("splash")
    val splash: Observable<SplashAdResponse>

    @get:GET("update")
    val updateInfo: Observable<BaseResponse<AppUpdate>>

    @GET("subjects/{pk}")
    fun getSubjectDetail(@Path("pk") pk: Int): Observable<SubjectDetailResponse>

    @GET("wallpapers")
    fun getSubjectWallpapers(@Query("subject_id") subjectId: Int,
                             @Query("limit") limit: Int,
                             @Query("offset") offset: Int): Observable<WallpaperPageResponse>

    @GET("wallpapers")
    fun getBannerWallpapers(@Query("banner_id") subjectId: Int,
                            @Query("limit") limit: Int,
                            @Query("offset") offset: Int): Observable<WallpaperPageResponse>

    @GET("wallpapers")
    fun getCategoryWallpapers(@Query("category_id") categoryId: Int,
                              @Query("limit") limit: Int,
                              @Query("offset") offset: Int): Observable<WallpaperPageResponse>

    /**
     * 获取推荐的WallPaper
     */
    @GET("recommend")
    fun getRecommendWallpapers(@Query("limit") limit: Int,
                               @Query("offset") offset: Int): Observable<WallpaperPageResponse>

    /**
     * 获取最新papers
     */
    @GET("wallpaper/newest")
    fun getNewestPapers(@Query("limit") limit: Int,
                        @Query("offset") offset: Int): Observable<WallpaperPageResponse>

    @GET("categories")
    fun getCategories(@Query("offset") offset: Int,
                      @Query("limit") limit: Int): Observable<CategoryPageResponse>

    @FormUrlEncoded
    @POST("account/register/")
    fun getRegisterAccount(@Field("phone") phone: String,
                           @Field("password") password: String): Observable<BaseResponse<Boolean>>

    @FormUrlEncoded
    @POST("account/login/")
    fun loginAccount(@Field("phone") phone: String): Observable<BaseResponse<MicroUser>>

    @POST("account/logout/")
    fun logout(): Observable<BaseResponse<String>>

    @GET("subjects/")
    fun searchKey(@Query("key") key: String,
                  @Query("limit") limit: Int,
                  @Query("offset") offset: Int): Observable<SubjectPageResponse>

    @GET("banners/")
    fun getBanners(@Query("limit") limit: Int,
                   @Query("offset") offset: Int): Observable<BannerPageResponse>

    @FormUrlEncoded
    @POST("category/update/")
    fun updateCategoryCover(@Field("cid") cid: Int,
                            @Field("logo") logo: String): Observable<BaseResponse<Boolean>>

    @FormUrlEncoded
    @POST("paper/set/banner")
    fun addPaper2Banner(@Field("bid") bid: Int,
                         @Field("pid") pid: Int): Observable<BaseResponse<Boolean>>

    @PATCH("user/update/{pk}")
    fun updateUser(@Body user: MicroUser, @Path("pk") pk: Int): Observable<MicroUser>

    @POST("buy/paper/{pk}")
    fun buyPaper(@Path("pk") pk: Int,
                 @Query("type") type: Int): Observable<BaseResponse<Int>>

    @GET("wallpaper/detail/{pk}")
    fun getPaperDetail(@Path("pk") pk: Int): Observable<BaseResponse<Wallpaper>> //    /**

    //     * collect
//     * */
//    @GET("collect/my")
//    Observable<WallpaperPageResponse>
//    getMyCollects(@Query("offset") int offset);
    companion object {
        const val PAGE_LIMIT = 20
        const val PAGE_START = 0
    }
}