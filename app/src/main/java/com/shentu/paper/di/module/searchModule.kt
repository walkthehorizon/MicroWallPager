package com.shentu.paper.di.module

import com.jess.arms.di.scope.ActivityScope
import com.shentu.paper.mvp.contract.SearchContract
import com.shentu.paper.mvp.model.SearchModel
import dagger.Module
import dagger.Provides


@Module
//构建searchModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class searchModule(private val view: SearchContract.View) {
    @ActivityScope
    @Provides
    fun providesearchView(): SearchContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun providesearchModel(model: SearchModel): SearchContract.Model {
        return model
    }
}
