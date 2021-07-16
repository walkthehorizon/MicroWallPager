package com.shentu.paper.di.module

import android.content.Context
import com.shentu.paper.mvp.contract.MyCollectContract
import com.shentu.paper.mvp.model.MyCollectModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@InstallIn(ActivityComponent::class)
@Module
//构建MyCollectModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class MyCollectModule() {
    @ActivityScoped
    @Provides
    fun provideMyCollectView(@ActivityContext context: Context): MyCollectContract.View {
        return context as MyCollectContract.View
    }

    @ActivityScoped
    @Provides
    fun provideMyCollectModel(model: MyCollectModel): MyCollectContract.Model {
        return model
    }
}
