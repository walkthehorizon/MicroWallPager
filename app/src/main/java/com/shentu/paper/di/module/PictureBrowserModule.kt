package com.shentu.paper.di.module

import android.content.Context
import com.shentu.paper.mvp.contract.PictureBrowserContract
import com.shentu.paper.mvp.model.PictureBrowserModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@InstallIn(ActivityComponent::class)
@Module
//构建PictureBrowserModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class PictureBrowserModule() {
    @ActivityScoped
    @Provides
    fun providePictureBrowserView(@ActivityContext context: Context): PictureBrowserContract.View {
        return context as PictureBrowserContract.View
    }

    @ActivityScoped
    @Provides
    fun providePictureBrowserModel(model: PictureBrowserModel): PictureBrowserContract.Model {
        return model
    }

}
