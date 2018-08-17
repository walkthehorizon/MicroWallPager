package com.shentu.wallpager.mvp.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpager.mvp.di.module.RankModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpager.mvp.ui.fragment.RankFragment

@FragmentScope
@Component(modules = arrayOf(RankModule::class), dependencies = arrayOf(AppComponent::class))
interface RankComponent {
    fun inject(fragment: RankFragment)
}
