package com.shentu.paper.di.module;

import com.jess.arms.di.scope.FragmentScope;
import com.shentu.paper.mvp.contract.TabHomeContract;
import com.shentu.paper.mvp.model.HotPagerModel;

import dagger.Module;
import dagger.Provides;


@Module
public class TabHomeModule {
    private TabHomeContract.View view;

    /**
     * 构建HotPagerModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public TabHomeModule(TabHomeContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    TabHomeContract.View provideHotPagerView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    TabHomeContract.Model provideHotPagerModel(HotPagerModel model) {
        return model;
    }
}