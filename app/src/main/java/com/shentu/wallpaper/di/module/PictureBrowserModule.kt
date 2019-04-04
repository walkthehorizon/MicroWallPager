package com.shentu.wallpaper.di.module

import com.jess.arms.di.scope.ActivityScope
import com.shentu.wallpaper.model.PictureBrowserModel
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import dagger.Module
import dagger.Provides


@Module
//构建PictureBrowserModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class PictureBrowserModule(private val view: PictureBrowserContract.View) {
    @ActivityScope
    @Provides
    fun providePictureBrowserView(): PictureBrowserContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun providePictureBrowserModel(model: PictureBrowserModel): PictureBrowserContract.Model {
        return model
    }

}
