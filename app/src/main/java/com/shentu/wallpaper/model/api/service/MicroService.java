package com.shentu.wallpaper.model.api.service;



import com.shentu.wallpaper.model.entity.CategoryListEntity;
import com.shentu.wallpaper.model.entity.CategorysEntity;
import com.shentu.wallpaper.model.entity.ResultResponse;
import com.shentu.wallpaper.model.entity.SplashAd;
import com.shentu.wallpaper.model.entity.SubjectDetailEntity;
import com.shentu.wallpaper.model.entity.SubjectsEntity;
import com.shentu.wallpaper.mvp.model.CategoryListModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MicroService {

    int PAGE_LIMIT = 18;

    @GET("splash")
    Observable<SplashAd> getSplash();

    @GET("subject/list")
    Observable<SubjectsEntity> getSubjectList(@Query("subject_type") int subjectType , @Query("limit") int limit, @Query("offset") int offset);

    @GET("subject/detail/{pk}")
    Observable<SubjectDetailEntity> getWallpaperBySubjectId(@Path("pk") int id, @Query("limit") int limit, @Query("offset") int offset);

    @GET("category/list")
    Observable<CategorysEntity> getCategorys();

    @GET("category/list/{id}")
    Observable<CategoryListEntity> getCategoryList(@Path("id")int id, @Query("limit") int limit, @Query("offset") int offset);

    @FormUrlEncoded
    @POST("account/register/")
    Observable<ResultResponse> getRegisterAccount(@Field("phone") String phone,@Field("password") String password);

    @FormUrlEncoded
    @POST("account/login/")
    Observable<ResultResponse> getLoginAccount(@Field("phone") String phone,@Field("password") String password);

    @POST("account/logout/")
    Observable<ResultResponse> getLogoutAccount();
}
