package com.shentu.wallpager.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpager.di.module.SettingMoreModule

import com.jess.arms.di.scope.ActivityScope
import com.shentu.wallpager.mvp.ui.activity.SettingMoreActivity

@ActivityScope
@Component(modules = arrayOf(SettingMoreModule::class), dependencies = arrayOf(AppComponent::class))
interface SettingMoreComponent {
    fun inject(activity: SettingMoreActivity)
}
