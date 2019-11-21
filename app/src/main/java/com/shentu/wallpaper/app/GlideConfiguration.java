///*
// * Copyright 2017 JessYan
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.shentu.wallpaper.app;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.GlideBuilder;
//import com.bumptech.glide.Registry;
//import com.bumptech.glide.annotation.GlideModule;
//import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
//import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
//import com.bumptech.glide.load.engine.cache.LruResourceCache;
//import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
//import com.bumptech.glide.load.model.GlideUrl;
//import com.bumptech.glide.module.AppGlideModule;
//import com.jess.arms.di.component.AppComponent;
//import com.jess.arms.http.OkHttpUrlLoader;
//import com.jess.arms.utils.ArmsUtils;
//import com.jess.arms.utils.DataHelper;
//
//import java.io.File;
//import java.io.InputStream;
//
///**
// * ================================================
// * {@link AppGlideModule} 的默认实现类
// * 用于配置缓存文件夹,切换图片请求框架等操作
// * ================================================
// */
//@GlideModule(glideName = "GlideArms")
//public class GlideConfiguration extends AppGlideModule {
//    public static final int IMAGE_DISK_CACHE_MAX_SIZE = 300 * 1024 * 1024;//图片缓存文件最大值为300Mb
//
//    @Override
//    public void applyOptions(Context context, GlideBuilder builder) {
//        final AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(context);
//        builder.setDiskCache(() -> {
//            // Careful: the external cache directory doesn't enforce permissions
//            return DiskLruCacheWrapper.create(DataHelper.makeDirs(new File(appComponent.cacheFile(), "Glide")), IMAGE_DISK_CACHE_MAX_SIZE);
//        });
//
//        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
//        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
//        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
//
//        int customMemoryCacheSize = (int) (1.5 * defaultMemoryCacheSize);
//        int customBitmapPoolSize = (int) (1.5 * defaultBitmapPoolSize);
//
//        builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
//        builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));
//    }
//
//    @Override
//    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
////        registry.replace(GlideUrl.class, InputStream.class, new com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader.Factory(HkApplication.getInstance().getOkHttpClient()));
//        //Glide 默认使用 HttpURLConnection 做网络请求,在这切换成 Okhttp 请求
//        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(context);
//        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(appComponent.okHttpClient()));
//    }
//
//    @Override
//    public boolean isManifestParsingEnabled() {
//        return false;
//    }
//}
