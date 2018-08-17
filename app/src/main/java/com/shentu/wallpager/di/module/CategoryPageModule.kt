package com.shentu.wallpager.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import com.shentu.wallpager.mvp.contract.CategoryPageContract
import com.shentu.wallpager.mvp.model.CategoryPageModel


@Module
//构建CategoryPageModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class CategoryPageModule(private val view: CategoryPageContract.View) {
    @FragmentScope
    @Provides
    fun provideCategoryPageView(): CategoryPageContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideCategoryPageModel(model: CategoryPageModel): CategoryPageContract.Model {
        return model
    }
}
