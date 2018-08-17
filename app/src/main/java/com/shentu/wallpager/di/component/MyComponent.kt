package com.shentu.wallpager.mvp.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpager.mvp.di.module.MyModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpager.mvp.ui.fragment.MyFragment

@FragmentScope
@Component(modules = arrayOf(MyModule::class), dependencies = arrayOf(AppComponent::class))
interface MyComponent {
    fun inject(fragment: MyFragment)
}
