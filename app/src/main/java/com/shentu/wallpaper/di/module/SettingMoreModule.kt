package com.shentu.wallpaper.di.module

import com.jess.arms.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

import com.shentu.wallpaper.mvp.contract.SettingMoreContract
import com.shentu.wallpaper.mvp.model.SettingMoreModel


@Module
//构建SettingMoreModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class SettingMoreModule(private val view: SettingMoreContract.View) {
    @ActivityScope
    @Provides
    fun provideSettingMoreView(): SettingMoreContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideSettingMoreModel(model: SettingMoreModel): SettingMoreContract.Model {
        return model
    }
}
