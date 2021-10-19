package com.shentu.paper.app.webview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.blankj.utilcode.util.ToastUtils;

public class AndroidInterface {

    private final Handler deliver = new Handler(Looper.getMainLooper());
    private final Context context;

    public AndroidInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void showImages(String[] images, int index) {
        ToastUtils.showShort("js调用");
    }
}
