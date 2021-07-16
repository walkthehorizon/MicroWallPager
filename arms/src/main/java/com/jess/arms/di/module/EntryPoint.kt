package com.jess.arms.di.module

import com.jess.arms.integration.ActivityLifecycle
import com.jess.arms.integration.ConfigModule
import com.jess.arms.integration.lifecycle.ActivityLifecycleForRxLifecycle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 *    date   : 2021/7/14 17:13
 *    author : mingming.li
 *    e-mail : mingming.li@ximalaya.com
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ConfigModuleEntryPoint {
    fun getConfigModules(): List<ConfigModule>
}

@EntryPoint
//@Named("ActivityLifecycle")
@InstallIn(SingletonComponent::class)
interface ActivityLifecycleEntryPoint {
    fun getActivityLifecycle(): ActivityLifecycle
}

@EntryPoint
//@Named("ActivityLifecycleForRxLifecycle")
@InstallIn(SingletonComponent::class)
interface ActivityRxLifecycleEntryPoint {
    fun getActivityRxLifecycle(): ActivityLifecycleForRxLifecycle
}
