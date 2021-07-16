package com.shentu.paper.di.module

import android.content.Context
import com.shentu.paper.mvp.contract.CategoryDetailContract
import com.shentu.paper.mvp.model.CategoryListModel
import com.shentu.paper.mvp.ui.fragment.CategoryListActivity
import com.shentu.paper.mvp.ui.fragment.CategoryListFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped


@InstallIn(FragmentComponent::class)
@Module
//构建CategoryListModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class CategoryListModule() {

    @FragmentScoped
    @Provides
    fun provideCategoryListView(@ActivityContext context: Context): CategoryDetailContract.View {
        return (context as CategoryListActivity).supportFragmentManager
            .findFragmentByTag(CategoryListFragment::class.simpleName) as CategoryListFragment
    }

    @FragmentScoped
    @Provides
    fun provideCategoryListModel(model: CategoryListModel): CategoryDetailContract.Model {
        return model
    }
}
