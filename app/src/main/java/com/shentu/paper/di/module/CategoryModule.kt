package com.shentu.paper.di.module

import android.content.Context
import com.shentu.paper.mvp.contract.CategoryContract
import com.shentu.paper.mvp.model.CategoryModel
import com.shentu.paper.mvp.ui.activity.MainActivity
import com.shentu.paper.mvp.ui.fragment.CategoryListActivity
import com.shentu.paper.mvp.ui.fragment.CategoryListFragment
import com.shentu.paper.mvp.ui.fragment.TabCategoryFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped

@InstallIn(FragmentComponent::class)
@Module
//构建CategoryModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class CategoryModule() {
    @FragmentScoped
    @Provides
    fun provideCategoryView(@ActivityContext context: Context): CategoryContract.View {
        return (context as MainActivity).supportFragmentManager
            .findFragmentByTag(TabCategoryFragment::class.simpleName) as TabCategoryFragment
    }

    @FragmentScoped
    @Provides
    fun provideCategoryModel(model: CategoryModel): CategoryContract.Model {
        return model
    }
}
