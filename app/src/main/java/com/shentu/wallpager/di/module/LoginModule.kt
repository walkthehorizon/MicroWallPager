package com.shentu.wallpager.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import com.shentu.wallpager.mvp.contract.LoginContract
import com.shentu.wallpager.mvp.model.LoginModel


@Module
//构建LoginModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class LoginModule(private val view: LoginContract.View) {
    @FragmentScope
    @Provides
    fun provideLoginView(): LoginContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideLoginModel(model: LoginModel): LoginContract.Model {
        return model
    }
}
