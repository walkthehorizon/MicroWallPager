package com.shentu.paper.di.module

import android.content.Context
import com.shentu.paper.mvp.contract.MyEditContract
import com.shentu.paper.mvp.model.MyEditModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@InstallIn(ActivityComponent::class)
@Module
//构建MyEditModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class MyEditModule {

    @ActivityScoped
    @Provides
    fun provideMyEditModel(model: MyEditModel): MyEditContract.Model {
        return model
    }

    @ActivityScoped
    @Provides
    fun provideMyEditView(@ActivityContext context: Context): MyEditContract.View {
        return context as MyEditContract.View
    }
}
