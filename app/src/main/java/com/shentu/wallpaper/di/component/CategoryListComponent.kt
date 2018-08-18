package com.shentu.wallpaper.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpaper.di.module.CategoryListModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.ui.fragment.CategoryListFragment

@FragmentScope
@Component(modules = arrayOf(CategoryListModule::class), dependencies = arrayOf(AppComponent::class))
interface CategoryListComponent {
    fun inject(fragment: CategoryListFragment)
}
