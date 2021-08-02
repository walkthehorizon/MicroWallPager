package com.micro.di.module

import com.micro.integration.ActivityLifecycle
import com.micro.integration.ConfigModule
import com.micro.integration.lifecycle.ActivityLifecycleForRxLifecycle
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
