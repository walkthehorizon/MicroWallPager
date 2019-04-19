package com.shentu.wallpaper.model.api.service;


import com.shentu.wallpaper.model.entity.BasePageResponse;
import com.shentu.wallpaper.model.entity.BaseResponse;
import com.shentu.wallpaper.model.entity.Subject;
import com.shentu.wallpaper.model.entity.User;
import com.shentu.wallpaper.model.response.CategoryPageResponse;
import com.shentu.wallpaper.model.response.SplashAdResponse;
import com.shentu.wallpaper.model.response.WallpaperPageResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MicroService {

    int PAGE_LIMIT = 20;

    @GET("splash")
    Observable<SplashAdResponse> getSplash();

    @GET("subjects")
    Observable<BaseResponse<BasePageResponse<Subject>>> getSubjects(@Query("subject_type") int subjectType, @Query("limit") int limit, @Query("offset") int offset);

    @GET("wallpapers")
    Observable<WallpaperPageResponse> getWallpapersBySubjectId(@Query("subject_id") int subjectId, @Query("limit") int limit, @Query("offset") int offset);

    @GET("wallpapers")
    Observable<WallpaperPageResponse> getWallpapersByCategoryId(@Query("category_id") int categoryId, @Query("limit") int limit, @Query("offset") int offset);

    /**
     * 获取推荐的WallPaper
     */
    @GET("recommend")
    Observable<WallpaperPageResponse> getRecommendWallpapers(
            @Query("limit") int limit,
            @Query("offset") int offset);

    @GET("categories")
    Observable<CategoryPageResponse> getCategories();

    @FormUrlEncoded
    @POST("account/register/")
    Observable<BaseResponse<Boolean>> getRegisterAccount(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded
    @POST("account/login/")
    Observable<BaseResponse<User>> getLoginAccount(@Field("phone") String phone, @Field("password") String password);

    @POST("account/logout/")
    Observable<BaseResponse<Boolean>> getLogoutAccount();
}
