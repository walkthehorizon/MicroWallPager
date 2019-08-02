package com.shentu.wallpaper.model.api.service;


import com.shentu.wallpaper.model.entity.MicroUser;
import com.shentu.wallpaper.model.response.BannerPageResponse;
import com.shentu.wallpaper.model.response.BaseResponse;
import com.shentu.wallpaper.model.response.CategoryPageResponse;
import com.shentu.wallpaper.model.response.SplashAdResponse;
import com.shentu.wallpaper.model.response.SubjectDetailResponse;
import com.shentu.wallpaper.model.response.SubjectPageResponse;
import com.shentu.wallpaper.model.response.WallpaperPageResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MicroService {

    int PAGE_LIMIT = 20;
    int PAGE_START = 0;

    @GET("splash")
    Observable<SplashAdResponse>
    getSplash();

    @GET("subjects/{pk}")
    Observable<SubjectDetailResponse>
    getSubjectDetail(@Path("pk") Integer pk);

    @GET("wallpapers")
    Observable<WallpaperPageResponse>
    getSubjectWallpapers(@Query("subject_id") int subjectId,
                         @Query("limit") int limit,
                         @Query("offset") int offset);

    @GET("wallpapers")
    Observable<WallpaperPageResponse>
    getBannerWallpapers(@Query("banner_id") int subjectId,
                        @Query("limit") int limit,
                        @Query("offset") int offset);

    @GET("wallpapers")
    Observable<WallpaperPageResponse>
    getCategoryWallpapers(@Query("category_id") int categoryId,
                          @Query("limit") int limit,
                          @Query("offset") int offset);

    /**
     * 获取推荐的WallPaper
     */
    @GET("recommend")
    Observable<WallpaperPageResponse>
    getRecommendWallpapers(@Query("limit") int limit,
                           @Query("offset") int offset);

    @GET("categories")
    Observable<CategoryPageResponse>
    getCategories(@Query("offset") int offset,
                  @Query("limit") int limit);

    @FormUrlEncoded
    @POST("account/register/")
    Observable<BaseResponse<Boolean>>
    getRegisterAccount(@Field("phone") String phone,
                       @Field("password") String password);

    @FormUrlEncoded
    @POST("account/login/")
    Observable<BaseResponse<MicroUser>>
    loginAccount(@Field("phone") String phone);

    @POST("account/logout/")
    Observable<BaseResponse<String>>
    logout();

    @GET("subjects/")
    Observable<SubjectPageResponse>
    searchKey(@Query("key") String key,
              @Query("limit") int limit,
              @Query("offset") int offset);

    @GET("banners/")
    Observable<BannerPageResponse>
    getBanners(@Query("limit") int limit,
               @Query("offset") int offset);

    @FormUrlEncoded
    @POST("category/update/")
    Observable<BaseResponse<Boolean>>
    updateCategoryCover(@Field("cid") Integer cid, @Field("logo") String logo);

    @PATCH("user/update/{pk}")
    Observable<MicroUser>
    updateUser(@Body MicroUser user, @Path("pk") int pk);

    @POST("buy/paper/{pk}")
    Observable<BaseResponse<String>>
    buyPaper(@Path("pk") int pk, @Query("pea") int pea);

//    /**
//     * collect
//     * */
//    @GET("collect/my")
//    Observable<WallpaperPageResponse>
//    getMyCollects(@Query("offset") int offset);
}
