package com.shentu.wallpaper.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpaper.di.module.CategoryModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.ui.fragment.CategoryFragment

@FragmentScope
@Component(modules = arrayOf(CategoryModule::class), dependencies = arrayOf(AppComponent::class))
interface CategoryComponent {
    fun inject(fragment: CategoryFragment)
}
