package com.shentu.wallpaper.di.module

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.contract.LoginVerifyContract
import com.shentu.wallpaper.mvp.model.LoginVerifyModel
import dagger.Module
import dagger.Provides


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
