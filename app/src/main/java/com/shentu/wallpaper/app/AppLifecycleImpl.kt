package com.shentu.wallpaper.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import butterknife.ButterKnife
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.horizon.netbus.NetBus
import com.jess.arms.base.delegate.AppLifecycles
import com.kingja.loadsir.core.LoadSir
import com.liulishuo.filedownloader.FileDownloader
import com.mob.MobSDK
import com.mob.moblink.MobLink
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.shentu.wallpaper.BuildConfig
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.config.Config
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.app.page.LoadingCallback
import com.shentu.wallpaper.mvp.ui.widget.MaterialFooter
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * ================================================
 * 展示 [AppLifecycles] 的用法
 * ================================================
 */
class AppLifecycleImpl : AppLifecycles {

    override fun attachBaseContext(base: Context) {
        MultiDex.install(base) //这里比 onCreate 先执行,常用于 MultiDex 初始化,插件化框架的初始化
    }

    @SuppressLint("CheckResult")
    override fun onCreate(application: Application) {
        instance = application
        if (BuildConfig.DEBUG) { //Timber初始化
            Timber.plant(DebugTree())
        }
        Utils.init(application)
        NetBus.getInstance().init(application)
        if (BuildConfig.DEBUG) { // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog() // 打印日志
            ARouter.openDebug() // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application) // 尽可能早，推荐在Application中初始化
        MobSDK.init(application)
        MobLink.setRestoreSceneListener(SceneListener())
        ButterKnife.setDebug(BuildConfig.DEBUG)
        FileDownloader.setup(application)
        BigImageViewer.initialize(GlideImageLoader.with(application
                , GlideConfiguration.imageClient))
        Config.initShake(application)
        LoadSir.beginBuilder()
                .addCallback(ErrorCallback()) //添加各种状态页
                .addCallback(EmptyCallback())
                .addCallback(LoadingCallback())
                .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
                .commit()
//        Thread(Runnable { init(application) }).start()
    }

    override fun onTerminate(application: Application) {}

    companion object {
        lateinit var instance: Application

        //static 代码段可以防止内存泄露
        init { //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context, _: RefreshLayout ->
                MaterialHeader(context).setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context, _: RefreshLayout? ->
                MaterialFooter(context)
            }
        }
    }
}