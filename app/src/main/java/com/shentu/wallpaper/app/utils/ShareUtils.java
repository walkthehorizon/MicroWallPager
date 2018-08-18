package com.shentu.wallpaper.app.utils;

import android.content.Context;

import com.shentu.wallpaper.R;

import javax.inject.Singleton;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareUtils {

    private static class Singleton {
        private static final ShareUtils instance = new ShareUtils();
    }

    public static ShareUtils getInstance() {
        return Singleton.instance;
    }

    public void showShare(Context context) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("分享标题");
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网使用
        oks.setComment("我是测试评论文本");
        // 启动分享GUI
        oks.show(context);
    }
}
