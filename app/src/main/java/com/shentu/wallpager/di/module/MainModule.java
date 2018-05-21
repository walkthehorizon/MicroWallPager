package com.shentu.wallpager.di.module;

import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;

import com.jess.arms.di.scope.ActivityScope;
import com.shentu.wallpager.R;
import com.shentu.wallpager.mvp.contract.MainContract;
import com.shentu.wallpager.mvp.model.MainModel;
import com.shentu.wallpager.mvp.ui.adapter.MainPagerAdapter;

import dagger.Module;
import dagger.Provides;


@Module
public class MainModule {
    private MainContract.View view;
    private SparseIntArray itemIds;

    /**
     * 构建MainModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public MainModule(MainContract.View view) {
        this.view = view;
        itemIds = new SparseIntArray(4);
        itemIds.put(0, R.id.navigation_hot);
        itemIds.put(1, R.id.navigation_category);
        itemIds.put(2, R.id.navigation_rank);
        itemIds.put(3, R.id.navigation_my);
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

    @ActivityScope
    @Provides
    MainPagerAdapter provideMainPageAdapter() {
        return new MainPagerAdapter(((AppCompatActivity) view).getSupportFragmentManager());
    }

    @ActivityScope
    @Provides
    SparseIntArray getItemIds() {
        return itemIds;
    }
}