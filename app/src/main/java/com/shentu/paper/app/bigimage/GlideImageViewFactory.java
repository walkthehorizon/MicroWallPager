package com.shentu.paper.app.bigimage;

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

public class GlideImageViewFactory extends ImageViewFactory {
    @Override
    protected final View createAnimatedImageView(final Context context, final int imageType,
            final int initScaleType) {
        switch (imageType) {
            case ImageInfoExtractor.TYPE_GIF: {
                final GifImageView gifImageView = new GifImageView(context);
                gifImageView.setScaleType(BigImageView.scaleType(initScaleType));
                return gifImageView;
            }
            case ImageInfoExtractor.TYPE_ANIMATED_WEBP: {
                final ImageView imageView = new ImageView(context);
                imageView.setScaleType(BigImageView.scaleType(initScaleType));
                return imageView;
            }
            default:
                return super.createAnimatedImageView(context, imageType, initScaleType);
        }
    }

    @Override
    public final void loadAnimatedContent(final View view, final int imageType,
            final File imageFile) {
        switch (imageType) {
            case ImageInfoExtractor.TYPE_GIF: {
                if (view instanceof GifImageView) {
                    ((GifImageView) view).setImageURI(
                            Uri.parse("file://" + imageFile.getAbsolutePath()));
                }
                break;
            }
            case ImageInfoExtractor.TYPE_ANIMATED_WEBP: {
                if (view instanceof ImageView) {
                    Glide.with(view.getContext())
                            .load(imageFile)
                            .into((ImageView) view);
                }
                break;
            }

            default:
                super.loadAnimatedContent(view, imageType, imageFile);
        }
    }

    @Override
    public void loadThumbnailContent(final View view, final Uri thumbnail) {
        if (view instanceof ImageView) {
            Glide.with(view.getContext())
                    .load(thumbnail)
                    .into((ImageView) view);
        }
    }
}
