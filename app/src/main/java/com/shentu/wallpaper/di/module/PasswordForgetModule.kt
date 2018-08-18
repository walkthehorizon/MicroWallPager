package com.shentu.wallpaper.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import com.shentu.wallpaper.mvp.contract.PasswordForgetContract
import com.shentu.wallpaper.model.PasswordForgetModel


@Module
//构建PasswordForgetModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class PasswordForgetModule(private val view: PasswordForgetContract.View) {
    @FragmentScope
    @Provides
    fun providePasswordForgetView(): PasswordForgetContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun providePasswordForgetModel(model: PasswordForgetModel): PasswordForgetContract.Model {
        return model
    }
}
