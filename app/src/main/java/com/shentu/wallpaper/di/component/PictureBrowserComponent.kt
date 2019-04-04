package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.ActivityScope
import com.shentu.wallpaper.di.module.PictureBrowserModule
import com.shentu.wallpaper.mvp.ui.activity.PictureBrowserActivity
import dagger.Component

@ActivityScope
@Component(modules = arrayOf(PictureBrowserModule::class), dependencies = arrayOf(AppComponent::class))
interface PictureBrowserComponent {
    fun inject(activity: PictureBrowserActivity)
}
