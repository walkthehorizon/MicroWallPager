package com.shentu.wallpaper.app.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;

import java.util.UUID;

public class HkUtils {

    private final String DEVICE_ID = "DEVICE_ID";
    public String deviceId;

    private static class SingletonHolder {
        private static final HkUtils INSTANCE = new HkUtils();
    }

    private HkUtils() {
        deviceId = SPUtils.getInstance().getString(DEVICE_ID, "");
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            SPUtils.getInstance().put(DEVICE_ID, deviceId);
        }
    }

    public static HkUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
