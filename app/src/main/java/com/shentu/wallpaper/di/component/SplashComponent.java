package com.shentu.wallpaper.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.shentu.wallpaper.di.module.SplashModule;
import com.shentu.wallpaper.mvp.ui.fragment.SplashFragment;

import dagger.Component;

@FragmentScope
@Component(modules = SplashModule.class, dependencies = AppComponent.class)
public interface SplashComponent {
    void inject(SplashFragment fragment);
}