
package com.shentu.wallpaper.model.api.service;


import com.shentu.wallpaper.model.entity.MicroUser;
import com.shentu.wallpaper.model.response.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface UserService {
    String HEADER_API_VERSION = "Accept: application/vnd.github.v3+json";

    @Headers({HEADER_API_VERSION})
    @GET("/users")
    Observable<List<MicroUser>> getUsers(@Query("since") int lastIdQueried, @Query("per_page") int perPage);

    @GET("sign")
    Observable<BaseResponse<Integer>>
    sign();
}
