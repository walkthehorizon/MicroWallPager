package com.shentu.wallpager.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpager.di.module.CategoryListModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpager.mvp.ui.fragment.CategoryListFragment

@FragmentScope
@Component(modules = arrayOf(CategoryListModule::class), dependencies = arrayOf(AppComponent::class))
interface CategoryListComponent {
    fun inject(fragment: CategoryListFragment)
}
