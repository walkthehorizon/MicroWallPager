package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.ActivityScope
import com.shentu.wallpaper.di.module.MyCollectModule
import com.shentu.wallpaper.mvp.ui.activity.MyCollectActivity
import dagger.Component

@ActivityScope
@Component(modules = arrayOf(MyCollectModule::class), dependencies = arrayOf(AppComponent::class))
interface MyCollectComponent {
    fun inject(activity: MyCollectActivity)
}
