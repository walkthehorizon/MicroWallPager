package com.shentu.wallpaper.app;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.piasy.biv.metadata.ImageInfoExtractor;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.ImageViewFactory;

import java.io.File;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Piasy{github.com/Piasy} on 2018/8/12.
 */
public class GlideImageViewFactory extends ImageViewFactory {
    @Override
    protected View createAnimatedImageView(final Context context, final int imageType,
                                           final File imageFile, int initScaleType) {
        switch (imageType) {
            case ImageInfoExtractor.TYPE_GIF:
                GifImageView gifImageView = new GifImageView(context);
                gifImageView.setImageURI(Uri.parse("file://" + imageFile.getAbsolutePath()));
                gifImageView.setScaleType(BigImageView.scaleType(initScaleType));
                return gifImageView;
            case ImageInfoExtractor.TYPE_ANIMATED_WEBP:
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(BigImageView.scaleType(initScaleType));
                Glide.with(context)
                        .load(imageFile)
                        .into(imageView);
                return imageView;
            default:
                return super.createAnimatedImageView(context, imageType, imageFile, initScaleType);
        }
    }

    @Override
    public View createThumbnailView(final Context context, final Uri thumbnail,
                                    final ImageView.ScaleType scaleType) {
        ImageView thumbnailView = new ImageView(context);
        if (scaleType != null) {
            thumbnailView.setScaleType(scaleType);
        }
        Glide.with(context)
                .load(thumbnail)
                .into(thumbnailView);
        return thumbnailView;
    }
}