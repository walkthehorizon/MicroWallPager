package com.shentu.wallpaper.model;

import android.app.Application;
import android.support.annotation.IntegerRes;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.shentu.wallpaper.app.BasePageModel;
import com.shentu.wallpaper.model.api.service.MicroService;
import com.shentu.wallpaper.model.entity.SubjectsEntity;
import com.shentu.wallpaper.mvp.contract.HotPagerContract;

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
    public Observable<SubjectsEntity> getSubjectList(int subjectType, boolean clear) {
        offset = clear ? 0 : (limit + offset);
        return mRepositoryManager
                .obtainRetrofitService(MicroService.class)
                .getSubjectList(subjectType,limit, offset);
    }

}