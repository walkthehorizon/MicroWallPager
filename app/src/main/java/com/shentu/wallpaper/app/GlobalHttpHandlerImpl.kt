package com.shentu.wallpaper.app

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.jess.arms.http.GlobalHttpHandler
import com.shentu.wallpaper.BuildConfig
import com.shentu.wallpaper.app.HkUserManager.token
import com.shentu.wallpaper.app.HkUserManager.uid
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber

/**
 * ================================================
 * 展示 [GlobalHttpHandler] 的用法
 *
 *
 * ================================================
 */
class GlobalHttpHandlerImpl(private val context: Context) : GlobalHttpHandler {
    override fun onHttpResultResponse(httpResult: String, chain: Interceptor.Chain, response: Response): Response {
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
        return response
    }

    // 这里可以在请求服务器之前可以拿到request,做一些操作比如给request统一添加token或者header以及参数加密等操作
    override fun onHttpRequestBefore(chain: Interceptor.Chain, request: Request): Request {
        Timber.e("load url: %s", request.url.toString())
        val builder = request.newBuilder()
        val token = token
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("Authorization", "Token $token")
        }
        val uid = uid
        if (uid != -1) {
            builder.addHeader("uid", uid.toString())
        }
        return builder
                .addHeader("Content-Type", "application/json")
                .addHeader("Device-Id", Settings.System.getString(context.contentResolver
                        , Settings.Secure.ANDROID_ID))
                .addHeader("System-Type", "Android")
                .addHeader("System-Version", Build.VERSION.RELEASE)
                .addHeader("Version-Name", BuildConfig.VERSION_NAME)
                .addHeader("Version-Code", BuildConfig.VERSION_CODE.toString()) //                .addHeader("X-CSRFToken", SPUtils.getInstance().getString("X-CSRFToken", ""))
                .build()
    }

}