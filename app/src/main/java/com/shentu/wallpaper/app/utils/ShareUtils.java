package com.shentu.wallpaper.app.utils;

import com.jess.arms.integration.AppManager;

import java.util.Objects;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by 神荼 on 2018/1/10.
 */

public class ShareUtils {

    private static final ShareUtils ourInstance = new ShareUtils();

    public static ShareUtils getInstance() {
        return ourInstance;
    }

    private ShareUtils() {
    }

    /**
     * 图文+Url链接分享
     *
     * @param text  分享的文本，不一定会显示
     * @param title 分享标题
     */
    public void showShare(String text, String title, String url, String imageUrl) {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setText(text);
        oks.setTitle(title);
        oks.setImageUrl(imageUrl);
        oks.setTitleUrl(url);
        oks.setUrl(url);
//        oks.setSite("http://wmmt119.top");//仅用于QQ空间
//        oks.setSiteUrl("http://wmmt119.top");//仅用于QQ空间
        oks.show(Objects.requireNonNull(AppManager.getAppManager().getTopActivity()));
    }

}
