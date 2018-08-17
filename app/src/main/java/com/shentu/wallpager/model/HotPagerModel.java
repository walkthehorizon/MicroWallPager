package com.shentu.wallpager.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.shentu.wallpager.app.BasePageModel;
import com.shentu.wallpager.mvp.contract.HotPagerContract;
import com.shentu.wallpager.mvp.model.api.service.MicroService;
import com.shentu.wallpager.mvp.model.entity.Subject;
import com.shentu.wallpager.mvp.model.entity.SubjectsEntity;
import com.shentu.wallpager.mvp.model.entity.Wallpaper;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;


@FragmentScope
public class HotPagerModel extends BasePageModel implements HotPagerContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public HotPagerModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<SubjectsEntity> getSubjectList(boolean clear) {
        offset = clear ? 0 : (limit + offset);
        return mRepositoryManager
                .obtainRetrofitService(MicroService.class)
                .getSubjectList(limit, offset);
    }
}