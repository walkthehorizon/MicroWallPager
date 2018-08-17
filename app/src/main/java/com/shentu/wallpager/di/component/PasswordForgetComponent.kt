package com.shentu.wallpager.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpager.di.module.PasswordForgetModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpager.mvp.ui.fragment.PasswordForgetFragment

@FragmentScope
@Component(modules = arrayOf(PasswordForgetModule::class), dependencies = arrayOf(AppComponent::class))
interface PasswordForgetComponent {
    fun inject(fragment: PasswordForgetFragment)
}
