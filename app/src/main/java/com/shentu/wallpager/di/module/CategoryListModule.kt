package com.shentu.wallpager.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import com.shentu.wallpager.mvp.contract.CategoryListContract
import com.shentu.wallpager.mvp.model.CategoryListModel


@Module
//构建CategoryListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class CategoryListModule(private val view: CategoryListContract.View) {
    @FragmentScope
    @Provides
    fun provideCategoryListView(): CategoryListContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideCategoryListModel(model: CategoryListModel): CategoryListContract.Model {
        return model
    }
}
