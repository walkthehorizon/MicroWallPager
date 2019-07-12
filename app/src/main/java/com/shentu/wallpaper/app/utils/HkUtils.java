package com.shentu.wallpaper.app.utils;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.SPUtils;
import com.shentu.wallpaper.BuildConfig;

import java.io.File;
import java.io.IOException;
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

    public static Uri getUriWithPath(Context context, String filepath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //7.0以上的读取文件uri要用这种方式了
            return FileProvider.getUriForFile(context.getApplicationContext(),
                    BuildConfig.APPLICATION_ID + ".fileprovider", new File(filepath));
        } else {
            return Uri.fromFile(new File(filepath));
        }
    }

    public static void setWallpaper(Context context, String path) {
        if (context == null || TextUtils.isEmpty(path)) {
            return;
        }
        Uri uriPath = getUriWithPath(context, path);
        Intent intent;
        if (RomUtil.isEmui()) {
            try {
                ComponentName componentName =
                        new ComponentName("com.android.gallery3d", "com.android.gallery3d.app.Wallpaper");
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    WallpaperManager.getInstance(context.getApplicationContext())
                            .setBitmap(BitmapFactory.decodeFile(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (RomUtil.isMiui()) {
            try {
                ComponentName componentName = new ComponentName("com.android.thememanager",
                        "com.android.thememanager.activity.WallpaperDetailActivity");
                intent = new Intent("miui.intent.action.START_WALLPAPER_DETAIL");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uriPath, "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.setComponent(componentName);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    WallpaperManager.getInstance(context.getApplicationContext())
                            .setBitmap(BitmapFactory.decodeFile(path));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            try {
                intent = WallpaperManager.getInstance(context.getApplicationContext()).getCropAndSetWallpaperIntent(uriPath);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            } catch (IllegalArgumentException e) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), uriPath);
                    if (bitmap != null) {
                        WallpaperManager.getInstance(context.getApplicationContext()).setBitmap(bitmap);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
