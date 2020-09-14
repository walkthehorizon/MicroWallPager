package com.shentu.paper.app.config

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.ItemListener
import com.afollestad.materialdialogs.list.listItems
import com.blankj.utilcode.util.*
import com.shentu.paper.BuildConfig
import com.shentu.paper.app.config.SensorHelper.OnShakeListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

object Config : LifecycleObserver {

    private val env: Environment = Environment.valueOf(SPUtils.getInstance().getString("Environment", if (BuildConfig.DEBUG) Environment.DEBUG.name else Environment.RELEASE.name))
    private lateinit var sensorHelper: SensorHelper
    private lateinit var context: Context

    fun init(context: Context) {
        if (env == Environment.RELEASE) {
            return
        }
        if (context !is Activity) {
            throw IllegalArgumentException("context is not activity")
        }
        this.context = context
        sensorHelper = SensorHelper(context)
        sensorHelper.setOnShakeListener(object : OnShakeListener {
            override fun onShake() {
                showEnvDialog()
            }
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        sensorHelper.stop()
        sensorHelper.setOnShakeListener(null)
    }

    val appServer: String
        get() = when (env) {
            Environment.DEBUG -> "http://192.168.10.122/micro/"
            Environment.PRE -> "https://api.wmmt119.top/micro/"
            else -> "https://api.wmmt119.top/micro/"
        }

    private fun showEnvDialog() {
        if (!AppUtils.isAppForeground()) {//应用不在前台，return
            return
        }
        MaterialDialog(ActivityUtils.getTopActivity()).show {
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