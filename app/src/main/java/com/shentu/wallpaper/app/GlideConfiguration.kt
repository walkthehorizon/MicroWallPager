package com.shentu.wallpaper.app

import android.content.Context
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.jess.arms.integration.OkHttpUrlLoader
import com.jess.arms.utils.ArmsUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File
import java.io.InputStream

/**
 * ================================================
 * [AppGlideModule] 的默认实现类
 * 用于配置缓存文件夹,切换图片请求框架等操作
 * ================================================
 */
@Excludes(OkHttpLibraryGlideModule::class)
@GlideModule(glideName = "GlideArms")
class GlideConfiguration : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val appComponent = ArmsUtils.obtainAppComponentFromContext(context)
        builder.setDiskCache {

            // Careful: the external cache directory doesn't enforce permissions
            val glide = File(appComponent.cacheFile(), IMAGE_DISK_CACHE_PATH)
            FileUtils.createOrExistsDir(glide)
            DiskLruCacheWrapper.create(glide, IMAGE_DISK_CACHE_MAX_SIZE.toLong())
        }
        val calculator = MemorySizeCalculator.Builder(context).build()
        val defaultMemoryCacheSize = calculator.memoryCacheSize
        val defaultBitmapPoolSize = calculator.bitmapPoolSize
        val customMemoryCacheSize = (1.5 * defaultMemoryCacheSize).toInt()
        val customBitmapPoolSize = (1.5 * defaultBitmapPoolSize).toInt()
        builder.setMemoryCache(LruResourceCache(customMemoryCacheSize.toLong()))
        builder.setBitmapPool(LruBitmapPool(customBitmapPoolSize.toLong()))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        //Glide 默认使用 HttpURLConnection 做网络请求,在这切换成 Okhttp 请求
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(imageClient))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    companion object {
        /**
         * 通常图片的域名往往是主域名（官网）的子域名，共用同一个泛匹配https签名，但免费签名不支持泛匹配，so,分隔处理
         */
        val imageClient: OkHttpClient
            get() {
                val builder = OkHttpClient.Builder()
                builder.addInterceptor(object : Interceptor {
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
                return builder.build()
            }

        const val IMAGE_DISK_CACHE_MAX_SIZE = 1024 * 1024 * 1024 //图片缓存文件最大值为1G
        const val IMAGE_DISK_CACHE_PATH = "image_manager_disk_cache"
    }
}