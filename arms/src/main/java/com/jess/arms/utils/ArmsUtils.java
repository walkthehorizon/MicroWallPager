
package com.jess.arms.utils;

import android.content.Intent;
import android.widget.Toast;

import com.jess.arms.integration.AppManager;

/**
 * ================================================
 * 一些框架常用的工具
 * ================================================
 */
public class ArmsUtils {
    static public Toast mToast;


    private ArmsUtils() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    /**
     * 跳转界面 1, 通过 {@link AppManager#startActivity(Class)}
     *
     * @param activityClass class
     */
    public static void startActivity(Class activityClass) {
        AppManager.getAppManager().startActivity(activityClass);
    }

    /**
     * 跳转界面 2, 通过 {@link AppManager#startActivity(Intent)}
     *
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

}
