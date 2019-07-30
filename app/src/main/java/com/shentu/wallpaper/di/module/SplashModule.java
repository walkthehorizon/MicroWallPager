package com.shentu.wallpaper.di.module;

import com.jess.arms.di.scope.FragmentScope;
import com.shentu.wallpaper.mvp.contract.SplashContract;
import com.shentu.wallpaper.mvp.model.SplashModel;

import dagger.Module;
import dagger.Provides;


@Module
public class SplashModule {
    private SplashContract.View view;

    /**
     * 构建SplashModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public SplashModule(SplashContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    SplashContract.View provideSplashView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    SplashContract.Model provideSplashModel(SplashModel model) {
        return model;
    }
}