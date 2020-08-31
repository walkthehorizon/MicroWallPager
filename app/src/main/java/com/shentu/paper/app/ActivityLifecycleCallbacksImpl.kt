package com.shentu.paper.app

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.view.View
import com.shentu.paper.R
import com.shentu.paper.mvp.ui.widget.DefaultToolbar
import timber.log.Timber

/**
 * ================================================
 * 展示 [Application.ActivityLifecycleCallbacks] 的用法
 * ================================================
 */
class ActivityLifecycleCallbacksImpl : ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Timber.w("$activity - onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.w("$activity - onActivityStarted")
        if (activity.findViewById<View?>(R.id.toolbar) != null) {
            if (activity.findViewById<View>(R.id.toolbar) !is DefaultToolbar) {
                return
            }
            val toolbar: DefaultToolbar = activity.findViewById(R.id.toolbar)
            toolbar.addOnClickListener(object : DefaultToolbar.OnClickListener() {
                override fun onClickLeftIcon() {
                    if (toolbar.isBack) activity.onBackPressed()
                }
            })
        }
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.w("$activity - onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.w("$activity - onActivityPaused")
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.w("$activity - onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
        Timber.w("$activity - onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.w("$activity - onActivityDestroyed")
        //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
        activity.intent.removeExtra("isInitToolbar")
    }
}