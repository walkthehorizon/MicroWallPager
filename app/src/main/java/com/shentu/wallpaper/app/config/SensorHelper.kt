package com.shentu.wallpaper.app.config

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils

class SensorHelper(context: Context) : SensorEventListener, LifecycleObserver {
    // 速度阈值，当摇晃速度达到这值后产生作用
    private val SPEED_SHRESHOLD = 8000

    // 两次检测的时间间隔
    private val UPTATE_INTERVAL_TIME = 50

    // 传感器管理器
    private var sensorManager: SensorManager? = null

    // 传感器
    private var sensor: Sensor? = null

    // 重力感应监听器
    private var onShakeListener: OnShakeListener? = null

    // 手机上一个位置时重力感应坐标
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()
    private var lastZ: Float = 0.toFloat()

    // 上次检测时间
    private var lastUpdateTime: Long = 0

    init {
        // 获得传感器管理器
        sensorManager = context
                .getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager != null) {
            // 获得重力传感器
            sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }

        // 注册
        sensorManager?.registerListener(
                this, sensor,
                SensorManager.SENSOR_DELAY_GAME
        )
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    // 摇晃监听接口
    interface OnShakeListener {
        fun onShake()
    }

    // 设置重力感应监听器
    fun setOnShakeListener(listener: OnShakeListener?) {
        onShakeListener = listener
    }

    /**
     * 重力感应器感应获得变化数据
     * android.hardware.SensorEventListener#onSensorChanged(android.hardware
     * .SensorEvent)
     */
    override fun onSensorChanged(event: SensorEvent) {
        // 现在检测时间
        val currentUpdateTime = System.currentTimeMillis()
        // 两次检测的时间间隔
        val timeInterval = currentUpdateTime - lastUpdateTime
        // 判断是否达到了检测时间间隔
        if (timeInterval < UPTATE_INTERVAL_TIME) return
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime
        // 获得x,y,z坐标
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        // 获得x,y,z的变化值
        val deltaX = x - lastX
        val deltaY = y - lastY
        val deltaZ = z - lastZ
        // 将现在的坐标变成last坐标
        lastX = x
        lastY = y
        lastZ = z
        val speed = Math.sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()) / timeInterval * 10000
        // 达到速度阀值，发出提示
        if (speed >= SPEED_SHRESHOLD) {
            onShakeListener?.onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

}