package com.shentu.wallpaper.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.shentu.wallpaper.di.module.HotPagerModule;
import com.shentu.wallpaper.mvp.ui.home.TabHomeFragment;

import dagger.Component;

@FragmentScope
@Component(modules = HotPagerModule.class, dependencies = AppComponent.class)
public interface HotPagerComponent {
    void inject(TabHomeFragment fragment);
}