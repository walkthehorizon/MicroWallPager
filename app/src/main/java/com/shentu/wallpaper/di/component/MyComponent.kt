package com.shentu.wallpaper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.FragmentScope
import com.shentu.wallpaper.di.module.MyModule
import com.shentu.wallpaper.mvp.ui.fragment.TabMyFragment
import dagger.Component

@FragmentScope
@Component(modules = arrayOf(MyModule::class), dependencies = arrayOf(AppComponent::class))
interface MyComponent {
    fun inject(fragment: TabMyFragment)
}
