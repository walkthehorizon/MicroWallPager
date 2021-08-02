
package com.micro.integration;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.micro.base.BaseFragment;
import com.micro.base.delegate.ActivityDelegate;
import com.micro.base.delegate.FragmentDelegate;
import com.micro.base.delegate.IActivity;
import com.micro.integration.cache.Cache;
import com.micro.integration.cache.IntelligentCache;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;


/**
 * ================================================
 * {@link Application.ActivityLifecycleCallbacks} 默认实现类
 * 通过 {@link ActivityDelegate} 管理 {@link Activity}
 * ================================================
 */
@Singleton
public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    @Inject
    Application mApplication;
    @Inject
    Cache<String, Object> mExtras;
    @Inject
    Lazy<FragmentManager.FragmentLifecycleCallbacks> mFragmentLifecycle;
    @Inject
    Lazy<List<FragmentManager.FragmentLifecycleCallbacks>> mFragmentLifecycles;

    @Inject
    public ActivityLifecycle() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //如果 intent 包含了此字段,并且为 true 说明不加入到 list 进行统一管理
        boolean isNotAdd = false;
        if (activity.getIntent() != null)
            isNotAdd = activity.getIntent().getBooleanExtra(AppManager.IS_NOT_ADD_ACTIVITY_LIST, false);

        if (!isNotAdd)
            AppManager.getAppManager().addActivity(activity);

        registerFragmentCallbacks(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        AppManager.getAppManager().setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (AppManager.getAppManager().getCurrentActivity() == activity) {
            AppManager.getAppManager().setCurrentActivity(null);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        AppManager.getAppManager().removeActivity(activity);
    }

    /**
     * 给每个 Activity 的所有 Fragment 设置监听其生命周期, Activity 可以通过 {@link IActivity#useFragment()}
     * 设置是否使用监听,如果这个 Activity 返回 false 的话,这个 Activity 下面的所有 Fragment 将不能使用 {@link FragmentDelegate}
     * 意味着 {@link BaseFragment} 也不能使用
     *
     * @param activity
     */
    private void registerFragmentCallbacks(Activity activity) {

        boolean useFragment = !(activity instanceof IActivity) || ((IActivity) activity).useFragment();
        if (activity instanceof FragmentActivity && useFragment) {

            //mFragmentLifecycle 为 Fragment 生命周期实现类, 用于框架内部对每个 Fragment 的必要操作, 如给每个 Fragment 配置 FragmentDelegate
            //注册框架内部已实现的 Fragment 生命周期逻辑
            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycle.get(), true);

            if (mExtras.containsKey(IntelligentCache.getKeyOfKeep(ConfigModule.class.getName()))) {
                List<ConfigModule> modules = (List<ConfigModule>) mExtras.get(IntelligentCache.getKeyOfKeep(ConfigModule.class.getName()));
                for (ConfigModule module : modules) {
                    module.injectFragmentLifecycle(mApplication, mFragmentLifecycles.get());
                }
                mExtras.remove(IntelligentCache.getKeyOfKeep(ConfigModule.class.getName()));
            }

            //注册框架外部, 开发者扩展的 Fragment 生命周期逻辑
            for (FragmentManager.FragmentLifecycleCallbacks fragmentLifecycle : mFragmentLifecycles.get()) {
                ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycle, true);
            }
        }
    }


}
