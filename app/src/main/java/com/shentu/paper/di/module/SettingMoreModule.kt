package com.shentu.paper.di.module

import android.content.Context
import com.shentu.paper.mvp.contract.SettingMoreContract
import com.shentu.paper.mvp.model.SettingMoreModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@InstallIn(ActivityComponent::class)
@Module
//构建SettingMoreModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class SettingMoreModule {
    @ActivityScoped
    @Provides
    fun provideSettingMoreView(@ActivityContext context: Context): SettingMoreContract.View {
        return context as SettingMoreContract.View
    }

    @ActivityScoped
    @Provides
    fun provideSettingMoreModel(model: SettingMoreModel): SettingMoreContract.Model {
        return model
    }
}
