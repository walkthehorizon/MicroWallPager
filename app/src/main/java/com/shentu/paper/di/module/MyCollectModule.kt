package com.shentu.paper.di.module

import com.jess.arms.di.scope.ActivityScope
import com.shentu.paper.mvp.contract.MyCollectContract
import com.shentu.paper.mvp.model.MyCollectModel
import dagger.Module
import dagger.Provides


@Module
//构建MyCollectModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class MyCollectModule(private val view: MyCollectContract.View) {
    @ActivityScope
    @Provides
    fun provideMyCollectView(): MyCollectContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideMyCollectModel(model: MyCollectModel): MyCollectContract.Model {
        return model
    }
}
