package com.shentu.paper.di.module

import android.content.Context
import com.shentu.paper.mvp.contract.MainContract
import com.shentu.paper.mvp.model.MainModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@InstallIn(ActivityComponent::class)
@Module
class MainModule {
    @ActivityScoped
    @Provides
    fun provideMainView(@ActivityContext context: Context): MainContract.View {
        return context as MainContract.View
    }

    @ActivityScoped
    @Provides
    fun provideMainModel(model: MainModel): MainContract.Model {
        return model
    }
}