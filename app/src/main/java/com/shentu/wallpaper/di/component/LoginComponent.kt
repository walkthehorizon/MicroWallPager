package com.shentu.wallpaper.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpaper.di.module.LoginModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.ui.fragment.LoginFragment

@FragmentScope
@Component(modules = arrayOf(LoginModule::class), dependencies = arrayOf(AppComponent::class))
interface LoginComponent {
    fun inject(fragment: LoginFragment)
}
