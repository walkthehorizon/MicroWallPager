package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.ActivityScope
import com.shentu.wallpaper.di.module.MyEditModule
import com.shentu.wallpaper.mvp.ui.my.MyEditActivity
import dagger.Component

@ActivityScope
@Component(modules = arrayOf(MyEditModule::class), dependencies = arrayOf(AppComponent::class))
interface MyEditComponent {
    fun inject(activity: MyEditActivity)
}
