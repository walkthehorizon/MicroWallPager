package com.shentu.wallpaper.app;

import com.bumptech.glide.Glide;
import com.jess.arms.base.BaseApplication;

import timber.log.Timber;

/**
 * 可以放置一些初始化内容，不要放置一些多处使用的参数等，避免继承第三方Application是转移困难
 */
public class HkApplication extends BaseApplication {
    private static HkApplication sInstance;

    @Override
    public void onCreate() {
//        Debug.startMethodTracing();
        super.onCreate();
        sInstance = this;
    }

    public static HkApplication getInstance() {
        return sInstance;
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
