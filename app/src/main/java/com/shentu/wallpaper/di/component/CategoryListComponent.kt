package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.di.module.CategoryListModule
import com.shentu.wallpaper.mvp.ui.fragment.CategoryListFragment
import dagger.Component

@FragmentScope
@Component(modules = arrayOf(CategoryListModule::class), dependencies = arrayOf(AppComponent::class))
interface CategoryListComponent {
    fun inject(fragment: CategoryListFragment)
}
