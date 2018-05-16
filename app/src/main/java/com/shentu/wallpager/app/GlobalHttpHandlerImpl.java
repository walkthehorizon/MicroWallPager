/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shentu.wallpager.app;

import android.content.Context;

import com.jess.arms.http.GlobalHttpHandler;
import com.shentu.wallpager.app.utils.HkUtils;

import java.net.URI;

import me.jessyan.mvparms.demo.BuildConfig;
import me.jessyan.mvparms.demo.app.utils.HkUtils;
import me.jessyan.mvparms.demo.di.HkUserManager;
import me.jessyan.mvparms.demo.mvp.model.SplashModel;
import me.jessyan.mvparms.demo.mvp.model.entity.User;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link GlobalHttpHandler} 的用法
 * <p>
 * Created by JessYan on 04/09/2017 17:06
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class GlobalHttpHandlerImpl implements GlobalHttpHandler {

    private Context context;

    public GlobalHttpHandlerImpl(Context context) {
        this.context = context;
    }

    @Override
    public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
                    /* 这里可以先客户端一步拿到每一次http请求的结果,可以解析成json,做一些操作,如检测到token过期后
                       重新请求token,并重新执行请求 */
//        if (!TextUtils.isEmpty(httpResult) && RequestInterceptor.isJson(response.body().contentType())) {
//            try {
//                List<User> list = ArmsUtils.obtainAppComponentFromContext(context).gson().fromJson(httpResult, new TypeToken<List<User>>() {
//                }.getType());
//                User user = list.get(0);
//                Timber.w("Result ------> " + user.getLogin() + "    ||   Avatar_url------> " + user.getAvatarUrl());
//            } catch (Exception e) {
//                e.printStackTrace();
//                return response;
//            }
//        }


                 /* 这里如果发现token过期,可以先请求最新的token,然后在拿新的token放入request里去重新请求
                    注意在这个回调之前已经调用过proceed,所以这里必须自己去建立网络请求,如使用okhttp使用新的request去请求
                    create a new request and modify it accordingly using the new token
                    Request newRequest = chain.request().newBuilder().header("token", newToken)
                                         .build();

                    retry the request

                    response.body().close();
                    如果使用okhttp将新的请求,请求成功后,将返回的response  return出去即可
                    如果不需要返回新的结果,则直接把response参数返回出去 */

        return response;
    }

    // 这里可以在请求服务器之前可以拿到request,做一些操作比如给request统一添加token或者header以及参数加密等操作
    @Override
    public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
        long extime = 5 * 60 + System.currentTimeMillis() / 1000;
        String token = getToken(extime, chain.request());
        return chain.request().newBuilder()
                .addHeader("uid", HkUserManager.getInstance().user.uid)
                .addHeader("deviceId", HkUtils.getInstance().deviceId)
                .addHeader("systemType", "Android")
                .addHeader("systemVersion", android.os.Build.VERSION.RELEASE)
                .addHeader("clientVersion", String.valueOf(BuildConfig.VERSION_NAME))
                .addHeader("appVersion", String.valueOf(BuildConfig.VERSION_CODE))
                .addHeader("apiVersion", "1")
                .addHeader("Extime", String.valueOf(extime))
                .addHeader("Apitoken", token)
                .build();
    }

    private String getToken(long l, Request request) {
        URI uri = request.url().uri();
        String replace = uri.getScheme() + "//" + uri.getHost() + "/" + uri.getPath();
        Timber.d("replace: " + replace);
        String token = HkUtils.getInstance().getSha1(BuildConfig.API_KEY + replace + Long.toHexString(l));
        token = token.toLowerCase();
        return token;
    }
}
