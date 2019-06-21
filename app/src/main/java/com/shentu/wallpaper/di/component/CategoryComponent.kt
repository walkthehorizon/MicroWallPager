package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.di.module.CategoryModule
import com.shentu.wallpaper.mvp.ui.fragment.TabCategoryFragment
import dagger.Component

@FragmentScope
@Component(modules = arrayOf(CategoryModule::class), dependencies = arrayOf(AppComponent::class))
interface CategoryComponent {
    fun inject(fragment: TabCategoryFragment)
}
