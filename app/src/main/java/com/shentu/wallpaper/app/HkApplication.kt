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

}