package com.shentu.wallpager.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.shentu.wallpager.di.module.HotPagerModule;
import com.shentu.wallpager.mvp.ui.fragment.HotPagerFragment;

import dagger.Component;

@FragmentScope
@Component(modules = HotPagerModule.class, dependencies = AppComponent.class)
public interface HotPagerComponent {
    void inject(HotPagerFragment fragment);
}