package com.shentu.wallpager.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.FragmentScope;
import com.shentu.wallpager.di.module.SplashModule;
import com.shentu.wallpager.mvp.ui.fragment.SplashFragment;

import dagger.Component;

@FragmentScope
@Component(modules = SplashModule.class, dependencies = AppComponent.class)
public interface SplashComponent {
    void inject(SplashFragment fragment);
}