package com.shentu.wallpager.mvp.model.api.service;

import com.shentu.wallpager.mvp.model.entity.CategorysEntity;
import com.shentu.wallpager.mvp.model.entity.SplashAd;
import com.shentu.wallpager.mvp.model.entity.Subject;
import com.shentu.wallpager.mvp.model.entity.SubjectDetailEntity;
import com.shentu.wallpager.mvp.model.entity.SubjectsEntity;
import com.shentu.wallpager.mvp.model.entity.Wallpaper;


import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MicroService {

    int PAGE_LIMIT = 20;

    @GET("splash")
    Observable<SplashAd> getSplash();

    @GET("subject/list")
    Observable<SubjectsEntity> getSubjectList(@Query("limit") int limit, @Query("offset") int offset);

    @GET("subject/detail/{pk}")
    Observable<SubjectDetailEntity> getWallpaperBySubjectId(@Path("pk") int id, @Query("limit") int limit, @Query("offset") int offset);

    @GET("category/list")
    Observable<CategorysEntity> getCategorys();
}
