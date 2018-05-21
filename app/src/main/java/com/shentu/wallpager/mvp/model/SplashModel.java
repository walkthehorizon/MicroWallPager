package com.shentu.wallpager.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.shentu.wallpager.mvp.contract.SplashContract;
import com.shentu.wallpager.mvp.model.api.service.CommonService;
import com.shentu.wallpager.mvp.model.entity.SplashAd;

import javax.inject.Inject;

import io.reactivex.Observable;


@FragmentScope
public class SplashModel extends BaseModel implements SplashContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public SplashModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<SplashAd> getSplashAd() {
        return mRepositoryManager
                .obtainRetrofitService(CommonService.class)
                .getSplashAd();
    }
}