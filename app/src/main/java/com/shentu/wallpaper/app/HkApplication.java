package com.shentu.wallpaper.app;

import com.bumptech.glide.Glide;
import com.jess.arms.base.BaseApplication;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import timber.log.Timber;

/**
 * 可以放置一些初始化内容，不要放置一些多处使用的参数等，避免继承第三方Application是转移困难
 */
public class HkApplication extends BaseApplication {
    private static HkApplication sInstance;
    private OkHttpClient client;

    @Override
    public void onCreate() {
//        Debug.startMethodTracing();
        super.onCreate();
        sInstance = this;
    }

    public static HkApplication getInstance() {
        return sInstance;
    }

    /**
     * 通常图片的域名往往是主域名（官网）的子域名，共用同一个泛匹配https签名，但免费签名不支持泛匹配，so,分隔处理
     */
    public OkHttpClient getImageClient() {
        if (client != null) {
            return client;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(chain -> {
            String url = chain.request().url().toString();
//            Timber.e("load image: %s", url);
            Request.Builder builder1 = chain.request().newBuilder();
            if (url.contains("wmmt119.top") || url.contains("myqcloud.com")) {
                builder1.addHeader("Referer", "wmmt119.top");
            }
            return chain.proceed(builder1.build());
        });
        client = builder.build();
        return client;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Timber.e("onTrimMemory: %s", level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }
}
