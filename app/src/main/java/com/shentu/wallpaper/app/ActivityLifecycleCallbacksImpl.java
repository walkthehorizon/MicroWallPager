
package com.shentu.wallpaper.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.shentu.wallpaper.R;
import com.shentu.wallpaper.mvp.ui.widget.DefaultToolbar;

import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link Application.ActivityLifecycleCallbacks} 的用法
 * <p>
 * Created by JessYan on 04/09/2017 17:14
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.w(activity + " - onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Timber.w(activity + " - onActivityStarted");
        if (activity.findViewById(R.id.toolbar) != null) {
            if (!(activity.findViewById(R.id.toolbar) instanceof DefaultToolbar)) {
                return;
            }
            DefaultToolbar toolbar = activity.findViewById(R.id.toolbar);
            toolbar.addOnClickListener(new DefaultToolbar.OnClickListener() {
                @Override
                public void onClickLeftIcon() {
                    if (toolbar.getLeftIcon() == R.drawable.ic_arrow_back_white_24dp) {
                        activity.onBackPressed();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Timber.w(activity + " - onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Timber.w(activity + " - onActivityPaused");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Timber.w(activity + " - onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Timber.w(activity + " - onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.w(activity + " - onActivityDestroyed");
        //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
        activity.getIntent().removeExtra("isInitToolbar");
    }
}
