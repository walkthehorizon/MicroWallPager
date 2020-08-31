package com.shentu.paper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.FragmentScope
import com.shentu.paper.di.module.HomeRankModule
import com.shentu.paper.mvp.ui.home.HomeRankFragment
import dagger.Component

@FragmentScope
@Component(modules = arrayOf(HomeRankModule::class), dependencies = arrayOf(AppComponent::class))
interface HomeRankComponent {
    fun inject(fragment: HomeRankFragment)
}
