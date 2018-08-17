package com.shentu.wallpager.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import com.shentu.wallpager.mvp.contract.LoginVerifyContract
import com.shentu.wallpager.model.LoginVerifyModel


@Module
//构建LoginVerifyModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class LoginVerifyModule(private val view: LoginVerifyContract.View) {
    @FragmentScope
    @Provides
    fun provideLoginVerifyView(): LoginVerifyContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideLoginVerifyModel(model: LoginVerifyModel): LoginVerifyContract.Model {
        return model
    }
}
