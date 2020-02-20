package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.di.module.HomeRankModule
import com.shentu.wallpaper.mvp.ui.home.HomeRankFragment
import dagger.Component

@FragmentScope
@Component(modules = arrayOf(HomeRankModule::class), dependencies = arrayOf(AppComponent::class))
interface HomeRankComponent {
    fun inject(fragment: HomeRankFragment)
}
