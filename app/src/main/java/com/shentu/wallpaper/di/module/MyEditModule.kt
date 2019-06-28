package com.shentu.wallpaper.di.module

import com.jess.arms.di.scope.ActivityScope
import com.shentu.wallpaper.mvp.contract.MyEditContract
import com.shentu.wallpaper.mvp.model.MyEditModel
import dagger.Module
import dagger.Provides


@Module
//构建MyEditModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class MyEditModule(private val view: MyEditContract.View) {
    @ActivityScope
    @Provides
    fun provideMyEditView(): MyEditContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideMyEditModel(model: MyEditModel): MyEditContract.Model {
        return model
    }
}
