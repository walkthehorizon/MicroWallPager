package com.shentu.paper.di.component

import com.jess.arms.di.component.AppComponent
import com.jess.arms.di.scope.FragmentScope
import com.shentu.paper.di.module.MyModule
import com.shentu.paper.mvp.ui.my.TabMyFragment
import dagger.Component

@FragmentScope
@Component(modules = arrayOf(MyModule::class), dependencies = arrayOf(AppComponent::class))
interface MyComponent {
    fun inject(fragment: TabMyFragment)
}
