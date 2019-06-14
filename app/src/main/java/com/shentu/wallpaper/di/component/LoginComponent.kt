package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.ActivityScope
import com.shentu.wallpaper.di.module.LoginModule
import com.shentu.wallpaper.mvp.ui.login.LoginActivity
import dagger.Component

@ActivityScope
@Component(modules = [LoginModule::class], dependencies = [AppComponent::class])
interface LoginComponent {
    fun inject(activity: LoginActivity)
}
