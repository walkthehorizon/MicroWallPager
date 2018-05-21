package com.shentu.wallpager.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.shentu.wallpager.di.module.MainModule;
import com.shentu.wallpager.mvp.ui.activity.MainActivity;

import dagger.Component;

@ActivityScope
@Component(modules = MainModule.class, dependencies = AppComponent.class)
public interface MainComponent {
    void inject(MainActivity activity);
}