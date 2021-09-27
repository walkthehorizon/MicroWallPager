package com.shentu.paper.app.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import cn.sharesdk.onekeyshare.OnekeyShare
import com.alibaba.android.arouter.launcher.ARouter
import com.shentu.paper.R
import com.shentu.paper.app.AppLifecycleImpl
import com.shentu.paper.app.Constant
import com.shentu.paper.app.HkApplication
import com.shentu.paper.model.entity.Wallpaper

/**
 * Created by 神荼 on 2018/1/10.
 */
object ShareUtils{
    /**
     * 图文+Url链接分享
     *
     * @param text  分享的文本，不一定会显示
     * @param title 分享标题
     */
    fun showShare(text: String?, title: String?, url: String?, imageUrl: String?) {
        val context: Context = AppLifecycleImpl.instance
        val oks = OnekeyShare()
        oks.disableSSOWhenAuthorize()
        oks.text = text
        oks.setTitle(title)
        oks.setImageUrl(imageUrl)
        oks.setTitleUrl(url)
        oks.setUrl(url)
        //        oks.setSite("http://wmmt119.top");//仅用于QQ空间
//        oks.setSiteUrl("http://wmmt119.top");//仅用于QQ空间
        oks.show(context)
    }

    /**
     * 展示分享九宫格
     * @param paper 带有详细数据的Wallpaper
     * */
    fun showShare(context: Context, paper: Wallpaper) {
        val oks = OnekeyShare()
        oks.disableSSOWhenAuthorize()
        oks.text = paper.description
        oks.setTitle(paper.title)
        oks.setImageUrl(paper.url)
        oks.setTitleUrl(Constant.BASE_WALLPAPER_SHARE_URL + paper.id)
        oks.setUrl(Constant.BASE_WALLPAPER_SHARE_URL + paper.id)
        oks.setDialogMode(false)
        oks.setSite(AppLifecycleImpl.instance.getString(R.string.app_name))
        oks.setSiteUrl(Constant.MICRO_BASE_URL)
        //自定义打开图片分享
        val customLogo = BitmapFactory.decodeResource(
            AppLifecycleImpl.instance.resources,
            R.drawable.ic_share_picture
        )
        val label = "分享图片"
        val listener = View.OnClickListener {
            ARouter.getInstance().build("/activity/picture/share")
                .withSerializable("paper", paper)
                .navigation(context)
        }
        oks.setCustomerLogo(customLogo, label, listener)
        oks.show(context)
    }
}