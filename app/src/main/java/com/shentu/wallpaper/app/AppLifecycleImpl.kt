package com.shentu.wallpaper.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import butterknife.ButterKnife
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.Utils
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.horizon.netbus.NetBus
import com.jess.arms.base.delegate.AppLifecycles
import com.jess.arms.integration.cache.IntelligentCache
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadSir
import com.liulishuo.filedownloader.FileDownloader
import com.mob.MobSDK
import com.mob.moblink.MobLink
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.shentu.wallpaper.BuildConfig
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.app.page.LoadingCallback
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
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
        if (BuildConfig.LOG_DEBUG) { //Timber初始化
            Timber.plant(DebugTree())
        }
        Thread(Runnable { init(application) }).start()
    }

    private fun init(application: Application) {
        val start = System.currentTimeMillis()
        Utils.init(application)
        NetBus.getInstance().init(application)
        if (BuildConfig.Debug) { // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog() // 打印日志
            ARouter.openDebug() // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application) // 尽可能早，推荐在Application中初始化
        MobSDK.init(application)
        MobLink.setRestoreSceneListener(SceneListener())
        ButterKnife.setDebug(BuildConfig.Debug)
        //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
//否则存储在 LRU 算法的存储空间中, 前提是 extras 使用的是 IntelligentCache (框架默认使用)
        ArmsUtils.obtainAppComponentFromContext(application).extras().put(IntelligentCache.KEY_KEEP
                + RefWatcher::class.java.name, if (BuildConfig.USE_CANARY) LeakCanary.install(application) else RefWatcher.DISABLED)
        LoadSir.beginBuilder()
                .addCallback(ErrorCallback()) //添加各种状态页
                .addCallback(EmptyCallback())
                .addCallback(LoadingCallback())
                .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
                .commit()
        FileDownloader.setup(application)
        BigImageViewer.initialize(GlideImageLoader.with(application
                , HkApplication.instance?.imageClient))
        Timber.d("init use time: %s ms", (System.currentTimeMillis() - start).toString())
    }

    override fun onTerminate(application: Application) {}

    companion object {
        //static 代码段可以防止内存泄露
        init { //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context, layout: RefreshLayout ->
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white) //全局设置主题颜色
                MaterialHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context, layout: RefreshLayout? ->
                ClassicsFooter(context).setDrawableSize(20f)
            }
        }
    }
}