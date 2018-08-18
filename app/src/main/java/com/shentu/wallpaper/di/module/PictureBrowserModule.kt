package com.shentu.wallpaper.di.module

import com.jess.arms.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.shentu.wallpaper.model.PictureBrowserModel
import com.shentu.wallpaper.mvp.ui.adapter.PictureBrowserVpAdapter


@Module
//构建PictureBrowserModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class PictureBrowserModule(private val view: PictureBrowserContract.View) {
    @FragmentScope
    @Provides
    fun providePictureBrowserView(): PictureBrowserContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun providePictureBrowserModel(model: PictureBrowserModel): PictureBrowserContract.Model {
        return model
    }

    @FragmentScope
    @Provides
    fun providePictureBrowserVpAdapter(): PictureBrowserVpAdapter{
        return PictureBrowserVpAdapter(ArrayList())
    }

}
