package com.shentu.wallpager.mvp.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpager.mvp.di.module.PictureBrowserModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpager.mvp.ui.fragment.PictureBrowserFragment

@FragmentScope
@Component(modules = arrayOf(PictureBrowserModule::class), dependencies = arrayOf(AppComponent::class))
interface PictureBrowserComponent {
    fun inject(fragment: PictureBrowserFragment)
}
