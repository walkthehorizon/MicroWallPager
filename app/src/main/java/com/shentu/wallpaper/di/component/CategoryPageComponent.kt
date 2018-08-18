package com.shentu.wallpaper.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpaper.di.module.CategoryPageModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.ui.fragment.CategoryPageFragment

@FragmentScope
@Component(modules = arrayOf(CategoryPageModule::class), dependencies = arrayOf(AppComponent::class))
interface CategoryPageComponent {
    fun inject(fragment: CategoryPageFragment)
}
