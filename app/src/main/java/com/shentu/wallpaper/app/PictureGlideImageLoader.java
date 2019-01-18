package com.shentu.wallpaper.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.loader.glide.GlideLoaderException;
import com.github.piasy.biv.loader.glide.GlideProgressSupport;
import com.github.piasy.biv.loader.glide.ImageDownloadTarget;
import com.github.piasy.biv.loader.glide.PrefetchTarget;
import com.github.piasy.biv.metadata.ImageInfoExtractor;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.OkHttpClient;

/**
 * 用于自定义大图加载的效果
 */
public class PictureGlideImageLoader implements ImageLoader {

    protected final RequestManager mRequestManager;

    private final ConcurrentHashMap<Integer, ImageDownloadTarget> mRequestTargetMap
            = new ConcurrentHashMap<>();

    protected PictureGlideImageLoader(Context context, OkHttpClient okHttpClient) {
        GlideProgressSupport.init(Glide.get(context), okHttpClient);
        mRequestManager = Glide.with(context);
    }

    public static PictureGlideImageLoader with(Context context) {
        return with(context, null);
    }

    public static PictureGlideImageLoader with(Context context, OkHttpClient okHttpClient) {
        return new PictureGlideImageLoader(context, okHttpClient);
    }

    @Override
    public void loadImage(final int requestId, final Uri uri, final ImageLoader.Callback callback) {
        ImageDownloadTarget target = new ImageDownloadTarget(uri.toString()) {
            @Override
            public void onResourceReady(@NonNull File resource,
                                        Transition<? super File> transition) {
                super.onResourceReady(resource, transition);
                // we don't need delete this image file, so it behaves like cache hit
                callback.onCacheHit(ImageInfoExtractor.getImageType(resource), resource);
                callback.onSuccess(resource);
            }

            @Override
            public void onLoadFailed(final Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                callback.onFail(new GlideLoaderException(errorDrawable));
            }

            @Override
            public void onDownloadStart() {
                callback.onStart();
            }

            @Override
            public void onProgress(int progress) {
                callback.onProgress(progress);
            }

            @Override
            public void onDownloadFinish() {
                callback.onFinish();
            }
        };
        clearTarget(requestId);
        saveTarget(requestId, target);

        downloadImageInto(uri, target);
    }

    protected void downloadImageInto(Uri uri, Target<File> target) {
        mRequestManager
                .downloadOnly()
                .load(uri)
                .into(target);
    }

    @Override
    public void prefetch(Uri uri) {
        downloadImageInto(uri, new PrefetchTarget());
    }

    @Override
    public void cancel(int requestId) {
        clearTarget(requestId);
    }

    private void saveTarget(int requestId, ImageDownloadTarget target) {
        mRequestTargetMap.put(requestId, target);
    }

    private void clearTarget(int requestId) {
        ImageDownloadTarget target = mRequestTargetMap.remove(requestId);
        if (target != null) {
            mRequestManager.clear(target);
        }
    }
}
