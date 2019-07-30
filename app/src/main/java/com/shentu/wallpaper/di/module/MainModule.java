package com.shentu.wallpaper.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.shentu.wallpaper.mvp.contract.MainContract;
import com.shentu.wallpaper.mvp.model.MainModel;

import dagger.Module;
import dagger.Provides;


@Module
public class MainModule {
    private MainContract.View view;
//    private SparseIntArray itemIds;

    /**
     * 构建MainModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MainModule(MainContract.View view) {
        this.view = view;
//        itemIds = new SparseIntArray(2);
//        itemIds.put(0, R.id.navigation_hot);
////        itemIds.put(1, R.id.navigation_category);
////        itemIds.put(2, R.id.navigation_rank);
//        itemIds.put(1, R.id.navigation_my);
    }

    @ActivityScope
    @Provides
    MainContract.View provideMainView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    MainContract.Model provideMainModel(MainModel model) {
        return model;
    }

//    @ActivityScope
//    @Provides
//    SparseIntArray getItemIds() {
//        return itemIds;
//    }
}