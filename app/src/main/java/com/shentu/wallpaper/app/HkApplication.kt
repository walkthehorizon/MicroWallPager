package com.shentu.wallpaper.app

import android.content.ComponentCallbacks2
import com.bumptech.glide.Glide
import com.jess.arms.base.BaseApplication
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import timber.log.Timber

/**
 * 可以放置一些初始化内容，不要放置一些多处使用的参数等，避免继承第三方Application是转移困难
 */
class HkApplication : BaseApplication() {
    private lateinit var client: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    /**
     * 通常图片的域名往往是主域名（官网）的子域名，共用同一个泛匹配https签名，但免费签名不支持泛匹配，so,分隔处理
     */
    val imageClient: OkHttpClient
        get() {
            if (this::client.isInitialized) {
                return this.client
            }
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(object : Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    val url = chain.request().url.toString()
                    //            Timber.e("load image: %s", url);
                    val builder1 = chain.request().newBuilder()
                    if (url.contains("wmmt119.top") || url.contains("myqcloud.com")) {
                        builder1.addHeader("Referer", "wmmt119.top")
                    }
                    return chain.proceed(builder1.build())
                }
            })
            client = builder.build()
            return client
        }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Timber.e("onTrimMemory: %s", level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory()
        }
        Glide.get(this).trimMemory(level)
    }

    companion object {
        lateinit var instance: HkApplication
    }
}