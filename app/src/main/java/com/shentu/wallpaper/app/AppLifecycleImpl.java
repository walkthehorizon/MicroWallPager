
package com.shentu.wallpaper.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.Utils;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.horizon.netbus.NetBus;
import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.utils.ArmsUtils;
import com.kingja.loadsir.core.LoadSir;
import com.liulishuo.filedownloader.FileDownloader;
import com.mob.MobSDK;
import com.mob.moblink.MobLink;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.shentu.wallpaper.BuildConfig;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.app.page.EmptyCallback;
import com.shentu.wallpaper.app.page.ErrorCallback;
import com.shentu.wallpaper.app.page.LoadingCallback;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import butterknife.ButterKnife;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link AppLifecycles} 的用法
 * ================================================
 */
public class AppLifecycleImpl implements AppLifecycles {

    public AppLifecycleImpl() {

    }

    @Override
    public void attachBaseContext(@NonNull Context base) {
        MultiDex.install(base);  //这里比 onCreate 先执行,常用于 MultiDex 初始化,插件化框架的初始化
    }


    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@NonNull Application application) {
        if (BuildConfig.LOG_DEBUG) {//Timber初始化
            Timber.plant(new Timber.DebugTree());
        }
        new Thread(() -> init(application)).start();
    }

    private void init(Application application) {
        long start = System.currentTimeMillis();
        Utils.init(application);
        NetBus.getInstance().init(application);
        if (BuildConfig.Debug) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application); // 尽可能早，推荐在Application中初始化
        MobSDK.init(application);
        MobLink.setRestoreSceneListener(new SceneListener());
        ButterKnife.setDebug(BuildConfig.Debug);

        //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
        //否则存储在 LRU 算法的存储空间中, 前提是 extras 使用的是 IntelligentCache (框架默认使用)
        ArmsUtils.obtainAppComponentFromContext(application).extras().put(IntelligentCache.KEY_KEEP
                + RefWatcher.class.getName(), BuildConfig.USE_CANARY ? LeakCanary.install(application) : RefWatcher.DISABLED);
        LoadSir.beginBuilder()
                .addCallback(new ErrorCallback())//添加各种状态页
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .setDefaultCallback(LoadingCallback.class)//设置默认状态页
                .commit();
        FileDownloader.setup(application);

        BigImageViewer.initialize(GlideImageLoader.with(application
                , HkApplication.getInstance().getImageClient()));
        Timber.d("init use time: %s ms", String.valueOf(System.currentTimeMillis() - start));
    }

    @Override
    public void onTerminate(@NonNull Application application) {

    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
            return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }
}
