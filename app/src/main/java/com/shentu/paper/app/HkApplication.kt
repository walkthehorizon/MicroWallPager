package com.shentu.paper.app

import android.os.StrictMode
import com.micro.base.BaseApplication
import dagger.hilt.android.HiltAndroidApp

/**
 * 如果需要继承其他的Application,必须实现BaseApplication中的逻辑
 * 因为Application无法通过热更修复，所以此种完全转移Application逻辑到代理的 AppLifecycleImpl 中
 * 可以完美实现Application生命周期内逻辑的热更能力
 * */
@HiltAndroidApp
class HkApplication : BaseApplication()