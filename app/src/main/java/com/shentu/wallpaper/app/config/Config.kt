package com.shentu.wallpaper.app.config

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.listItems
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.SPUtils
import com.shentu.wallpaper.BuildConfig
import com.shentu.wallpaper.app.config.SensorHelper.OnShakeListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

object Config {

    private var env: Environment = Environment.valueOf(SPUtils.getInstance().getString("Environment", if (BuildConfig.DEBUG) Environment.DEBUG.name else Environment.RELEASE.name))

    fun initShake(context: Context) {
        if (env != Environment.RELEASE) {
            val helper = SensorHelper(context)
            helper.setOnShakeListener(object : OnShakeListener {
                override fun onShake() {
                    showEnvDialog(context)
                }
            })
        }
    }

    val appServer: String
        get() = when (env) {
            Environment.DEBUG -> "http://192.168.10.122/micro/"
            else -> "http://api.wmmt119.top/micro/"
        }

    private fun showEnvDialog(context: Context) {
        MaterialDialog(context).show {
            title(text = env.name)
            listItems(items = listOf(Environment.DEBUG.name, Environment.PRE.name), waitForPositiveButton = false
                    , selection = object : ItemListener {
                override fun invoke(dialog: MaterialDialog, index: Int, text: String) {
                    SPUtils.getInstance().put("Environment", text)
                    Timber.e("env:%s", SPUtils.getInstance().getString("Environment"))
                    if (env.name != text) {
                        GlobalScope.launch {
                            delay(500)
                            AppUtils.relaunchApp(true)
                        }
                    }
                }
            })
        }
    }
}