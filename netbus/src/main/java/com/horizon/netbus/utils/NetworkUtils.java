package com.horizon.netbus.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.horizon.netbus.NetBus;
import com.horizon.netbus.NetType;

public class NetworkUtils {
    /**
     * 网络是否可用
     *
     * @return
     */
    @SuppressWarnings("MissingPermission")
    public static boolean isAvailable() {
        ConnectivityManager connmagr = (ConnectivityManager) NetBus.getInstance().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connmagr == null) {
            return false;
        }
        NetworkInfo[] allNetworkInfo = connmagr.getAllNetworkInfo();
        if (allNetworkInfo != null) {
            for (NetworkInfo networkInfo : allNetworkInfo) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取网络类型
     *
     * @return
     */
    public static NetType getNetType() {
        ConnectivityManager connmagr = (ConnectivityManager) NetBus.getInstance().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connmagr == null) {
            return NetType.NONE;
        }
        NetworkInfo activeNetworkInfo = connmagr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            int type = activeNetworkInfo.getType();
            if (type == ConnectivityManager.TYPE_MOBILE) {
                return NetType.GPRS;
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                return NetType.WIFI;

            }
        }
        return NetType.NONE;
    }

    public static void openNetSetting(Activity context, int code) {
        Intent intent = new Intent("/");
        ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(componentName);
        context.startActivityForResult(intent, code);
    }
}