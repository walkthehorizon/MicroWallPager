package com.shentu.wallpaper.app.utils

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.SPUtils
import com.jess.arms.integration.AppManager
import com.shentu.wallpaper.BuildConfig
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

class HkUtils private constructor() {

    private val DEVICE_ID = "DEVICE_ID"
    var deviceId: String

    private object SingletonHolder {
        val INSTANCE = HkUtils()
    }

    init {
        deviceId = SPUtils.getInstance().getString(DEVICE_ID, "")
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString()
            SPUtils.getInstance().put(DEVICE_ID, deviceId)
        }
    }

    fun showChargeDialog(context: Context) {
        MaterialDialog(context).show {
            title(text = "看豆获取")
            message(text = "看豆，看豆，在多也不够！")
            positiveButton(text = "打赏获取") {
                contactKefu()
            }
            negativeButton(text = "还是算了")
        }
    }

    companion object {

        val instance: HkUtils
            get() = SingletonHolder.INSTANCE

        fun contactKefu() {
            try {
                AppManager.getAppManager().startActivity((Intent(Intent.ACTION_VIEW,
                        Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=160585515"))))
            } catch (e: ClassNotFoundException) {
                Timber.e(e)
            }
        }

        private fun getUriWithPath(context: Context, filepath: String): Uri {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //7.0以上的读取文件uri要用这种方式了
                FileProvider.getUriForFile(context.applicationContext,
                        BuildConfig.APPLICATION_ID + ".fileProvider", File(filepath))
            } else {
                Uri.fromFile(File(filepath))
            }
        }

        fun setWallpaper(context: Context?, path: String) {
            if (context == null || TextUtils.isEmpty(path)) {
                return
            }
            val uriPath = getUriWithPath(context, path)
            val intent: Intent
            when {
                RomUtil.isEmui() -> try {
                    val componentName = ComponentName("com.android.gallery3d", "com.android.gallery3d.app.Wallpaper")
                    intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.setDataAndType(uriPath, "image/*")
                    intent.putExtra("mimeType", "image/*")
                    intent.component = componentName
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        WallpaperManager.getInstance(context.applicationContext)
                                .setBitmap(BitmapFactory.decodeFile(path))
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                RomUtil.isMiui() -> try {
                    val componentName = ComponentName("com.android.thememanager",
                            "com.android.thememanager.activity.WallpaperDetailActivity")
                    intent = Intent("miui.intent.action.START_WALLPAPER_DETAIL")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.setDataAndType(uriPath, "image/*")
                    intent.putExtra("mimeType", "image/*")
                    intent.component = componentName
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        WallpaperManager.getInstance(context.applicationContext)
                                .setBitmap(BitmapFactory.decodeFile(path))
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                else -> try {
                    intent = WallpaperManager.getInstance(context.applicationContext).getCropAndSetWallpaperIntent(uriPath)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.applicationContext.startActivity(intent)
                } catch (e: IllegalArgumentException) {
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(context.applicationContext.contentResolver, uriPath)
                        if (bitmap != null) {
                            WallpaperManager.getInstance(context.applicationContext).setBitmap(bitmap)
                        }
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
            }
        }
    }
}
