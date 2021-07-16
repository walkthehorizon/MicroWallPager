package com.shentu.paper.di.module

import android.content.Context
import com.shentu.paper.mvp.contract.SearchContract
import com.shentu.paper.mvp.model.SearchModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@InstallIn(ActivityComponent::class)
@Module
//构建searchModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class SearchModule() {
    @ActivityScoped
    @Provides
    fun providesearchView(@ActivityContext context: Context): SearchContract.View {
        return context as SearchContract.View
    }

    @ActivityScoped
    @Provides
    fun providesearchModel(model: SearchModel): SearchContract.Model {
        return model
    }
}
