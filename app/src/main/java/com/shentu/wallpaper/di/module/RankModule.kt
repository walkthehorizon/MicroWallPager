package com.shentu.wallpaper.di.module

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.contract.RankContract
import com.shentu.wallpaper.mvp.model.RankModel
import dagger.Module
import dagger.Provides


@Module
//构建RankModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class RankModule(private val view: RankContract.View) {
    @FragmentScope
    @Provides
    fun provideRankView(): RankContract.View {
        return this.view
    }

    @FragmentScope
    @Provides
    fun provideRankModel(model: RankModel): RankContract.Model {
        return model
    }
}
