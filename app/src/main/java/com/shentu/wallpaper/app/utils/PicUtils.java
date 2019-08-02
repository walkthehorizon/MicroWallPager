package com.shentu.wallpaper.app.utils;

import android.text.TextUtils;
import android.webkit.URLUtil;

import com.blankj.utilcode.util.PathUtils;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.io.File;

public class PicUtils {
    private static final PicUtils ourInstance = new PicUtils();

    public static PicUtils getInstance() {
        return ourInstance;
    }

    private PicUtils() {
    }

    public GlideUrl getMM131GlideUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            return new GlideUrl(url, new LazyHeaders.Builder()
                    .addHeader("Referer", "http://www.mm131.com/")
                    .build());
        }
    }

    public String buildtl640(String picUrl) {
        return picUrl + "!tl640";
    }

    public String buildtl200(String picUrl) {
        return picUrl + "!tl200";
    }

    public String getDownloadPicturePath(String pictureUrl) {
        return PathUtils.getExternalDcimPath() + File.separator + "看个够" + File.separator
                + URLUtil.guessFileName(pictureUrl, null, null);
    }

    public String getSetPaperCachePath(String pictureUrl) {
        return PathUtils.getInternalAppCachePath() + File.separator + "Wallpaper" + File.separator
                + URLUtil.guessFileName(pictureUrl, null, null);
    }
}
