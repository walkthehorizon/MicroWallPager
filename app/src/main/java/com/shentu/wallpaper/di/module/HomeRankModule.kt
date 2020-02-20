package com.shentu.wallpaper.di.module

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.contract.HomeRankContract
import com.shentu.wallpaper.mvp.model.HomeRankModel
import dagger.Module
import dagger.Provides


@Module
//构建HomeRankModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class HomeRankModule(private val view: HomeRankContract.View) {
    @FragmentScope
    @Provides
    fun provideHomeRankView(): HomeRankContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideHomeRankModel(model: HomeRankModel): HomeRankContract.Model {
        return model
    }
}
