package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.ActivityScope
import com.shentu.wallpaper.di.module.SubjectDetailModule
import com.shentu.wallpaper.mvp.ui.activity.SubjectDetailActivity
import dagger.Component

@ActivityScope
@Component(modules = arrayOf(SubjectDetailModule::class), dependencies = arrayOf(AppComponent::class))
interface SubjectDetailComponent {
    fun inject(activity: SubjectDetailActivity)
}
