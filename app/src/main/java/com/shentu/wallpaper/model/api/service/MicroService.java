package com.shentu.wallpaper.model.api.service;


import com.shentu.wallpaper.model.entity.BasePageResponse;
import com.shentu.wallpaper.model.entity.BaseResponse;
import com.shentu.wallpaper.model.entity.Category;
import com.shentu.wallpaper.model.entity.SplashAd;
import com.shentu.wallpaper.model.entity.Subject;
import com.shentu.wallpaper.model.entity.User;
import com.shentu.wallpaper.model.entity.Wallpaper;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MicroService {

    int PAGE_LIMIT = 20;

    @GET("splash")
    Observable<SplashAd> getSplash();

    @GET("subject/list")
    Observable<BasePageResponse<Subject>> getSubjects(@Query("subject_type") int subjectType , @Query("limit") int limit, @Query("offset") int offset);

    @GET("subject/detail/{pk}")
    Observable<BasePageResponse<List<Wallpaper>>> getWallpapersBySubjectId(@Path("pk") int id, @Query("limit") int limit, @Query("offset") int offset);

    @GET("category/list")
    Observable<BasePageResponse<Category>> getCategorys();

    @GET("category/list/{id}")
    Observable<BaseResponse<Category>> getCategoryById(@Path("id")int id, @Query("limit") int limit, @Query("offset") int offset);

    @FormUrlEncoded
    @POST("account/register/")
    Observable<BaseResponse<Boolean>> getRegisterAccount(@Field("phone") String phone,@Field("password") String password);

    @FormUrlEncoded
    @POST("account/login/")
    Observable<BaseResponse<User>> getLoginAccount(@Field("phone") String phone, @Field("password") String password);

    @POST("account/logout/")
    Observable<BaseResponse<Boolean>> getLogoutAccount();
}
