package com.shentu.wallpaper.di.component

import dagger.Component
import com.jess.arms.di.component.AppComponent

import com.shentu.wallpaper.di.module.PictureBrowserModule

import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.mvp.ui.fragment.PictureBrowserFragment

@FragmentScope
@Component(modules = arrayOf(PictureBrowserModule::class), dependencies = arrayOf(AppComponent::class))
interface PictureBrowserComponent {
    fun inject(fragment: PictureBrowserFragment)
}
