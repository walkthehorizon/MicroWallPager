package com.shentu.wallpaper.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpaper.di.module.LoginVerifyModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.ui.fragment.LoginVerifyFragment

@FragmentScope
@Component(modules = arrayOf(LoginVerifyModule::class), dependencies = arrayOf(AppComponent::class))
interface LoginVerifyComponent {
    fun inject(fragment: LoginVerifyFragment)
}
