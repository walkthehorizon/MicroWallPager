package com.shentu.wallpaper.di.module

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.contract.CategoryDetailContract
import com.shentu.wallpaper.mvp.model.CategoryListModel
import dagger.Module
import dagger.Provides


@Module
//构建CategoryListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class CategoryListModule(private val view: CategoryDetailContract.View) {
    @FragmentScope
    @Provides
    fun provideCategoryListView(): CategoryDetailContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideCategoryListModel(model: CategoryListModel): CategoryDetailContract.Model {
        return model
    }
}
