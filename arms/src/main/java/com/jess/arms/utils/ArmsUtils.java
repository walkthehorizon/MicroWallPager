
package com.jess.arms.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.arms.base.App;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.AppManager;

import java.security.MessageDigest;

/**
 * ================================================
 * 一些框架常用的工具
 * <p>
 * Created by JessYan on 2015/11/23.
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class ArmsUtils {
    static public Toast mToast;


    private ArmsUtils() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    /**
     * 使用 {@link Snackbar} 显示文本消息
     * Arms 已将 com.android.support:design 从依赖中移除 (目的是减小 Arms 体积, design 库中含有太多 View)
     * 因为 Snackbar 在 com.android.support:design 库中, 所以如果框架使用者没有自行依赖 com.android.support:design
     * Arms 则会使用 Toast 替代 Snackbar 显示信息, 如果框架使用者依赖了 arms-autolayout 库就不用依赖 com.android.support:design 了
     * 因为在 arms-autolayout 库中已经依赖有 com.android.support:design
     *
     * @param text
     */
    public static void snackbarText(String text) {
        AppManager.getAppManager().showSnackbar(text, false);
    }

    /**
     * 跳转界面 1, 通过 {@link AppManager#startActivity(Class)}
     *
     * @param activityClass
     */
    public static void startActivity(Class activityClass) {
        AppManager.getAppManager().startActivity(activityClass);
    }

    /**
     * 跳转界面 2, 通过 {@link AppManager#startActivity(Intent)}
     *
     * @param
     */
    public static void startActivity(Intent content) {
        AppManager.getAppManager().startActivity(content);
    }


    /**
     * 执行 {@link AppManager#killAll()}
     */
    public static void killAll() {
        AppManager.getAppManager().killAll();
    }

    /**
     * 执行 {@link AppManager#appExit()}
     */
    public static void exitApp() {
        AppManager.getAppManager().appExit();
    }

    public static AppComponent obtainAppComponentFromContext(Context context) {
        Preconditions.checkNotNull(context, "%s cannot be null", Context.class.getName());
        Preconditions.checkState(context.getApplicationContext() instanceof App, "Application does not implements App");
        return ((App) context.getApplicationContext()).getAppComponent();
    }

}
