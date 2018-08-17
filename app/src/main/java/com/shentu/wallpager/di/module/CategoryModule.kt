package com.shentu.wallpager.mvp.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import com.shentu.wallpager.mvp.contract.CategoryContract
import com.shentu.wallpager.mvp.model.CategoryModel


@Module
//构建CategoryModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class CategoryModule(private val view: CategoryContract.View) {
    @FragmentScope
    @Provides
    fun provideCategoryView(): CategoryContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideCategoryModel(model: CategoryModel): CategoryContract.Model {
        return model
    }
}
