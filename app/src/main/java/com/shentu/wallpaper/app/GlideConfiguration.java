
package com.shentu.wallpaper.app;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.Excludes;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.OkHttpUrlLoader;
import com.jess.arms.utils.ArmsUtils;

import java.io.File;
import java.io.InputStream;

/**
 * ================================================
 * {@link AppGlideModule} 的默认实现类
 * 用于配置缓存文件夹,切换图片请求框架等操作
 * ================================================
 */
@Excludes({OkHttpLibraryGlideModule.class})
@GlideModule(glideName = "GlideArms")
public class GlideConfiguration extends AppGlideModule {
    public static final int IMAGE_DISK_CACHE_MAX_SIZE = 1024 * 1024 * 1024;//图片缓存文件最大值为1G

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        final AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(context);
        builder.setDiskCache(() -> {
            // Careful: the external cache directory doesn't enforce permissions
            File glide = new File(appComponent.cacheFile(), "image_manager_disk_cache");
            FileUtils.createOrExistsDir(glide);
            return DiskLruCacheWrapper.create(glide, IMAGE_DISK_CACHE_MAX_SIZE);
        });

        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

        int customMemoryCacheSize = (int) (1.5 * defaultMemoryCacheSize);
        int customBitmapPoolSize = (int) (1.5 * defaultBitmapPoolSize);

        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        //Glide 默认使用 HttpURLConnection 做网络请求,在这切换成 Okhttp 请求
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(
                HkApplication.getInstance().getImageClient()));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
