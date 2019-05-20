package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.ActivityScope
import com.shentu.wallpaper.di.module.searchModule
import com.shentu.wallpaper.mvp.ui.activity.SearchActivity
import dagger.Component

@ActivityScope
@Component(modules = arrayOf(searchModule::class), dependencies = arrayOf(AppComponent::class))
interface searchComponent {
    fun inject(activity: SearchActivity)
}
