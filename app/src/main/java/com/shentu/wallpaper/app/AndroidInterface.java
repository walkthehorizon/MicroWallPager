package com.shentu.wallpaper.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.just.agentweb.AgentWeb;
import com.shentu.wallpaper.mvp.ui.browser.PictureBrowserActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private Context context;

    public AndroidInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void showImages(String[] images, int index) {
        ToastUtils.showShort("js调用");
    }
}
