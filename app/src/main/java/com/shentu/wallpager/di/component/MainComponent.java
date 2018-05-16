package com.shentu.wallpager.di.component;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import me.jessyan.mvparms.demo.di.module.MainModule;

import com.jess.arms.di.scope.ActivityScope;
import com.shentu.wallpager.di.module.MainModule;
import com.shentu.wallpager.mvp.ui.activity.MainActivity;

import me.jessyan.mvparms.demo.mvp.ui.activity.MainActivity;

@ActivityScope
@Component(modules = MainModule.class, dependencies = AppComponent.class)
public interface MainComponent {
    void inject(MainActivity activity);
}