package com.shentu.wallpaper.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.shentu.wallpaper.model.entity.SubjectsEntity;
import com.shentu.wallpaper.mvp.contract.HotPagerContract;


import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;


@FragmentScope
public class HotPagerPresenter extends BasePresenter<HotPagerContract.Model, HotPagerContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public HotPagerPresenter(HotPagerContract.Model model, HotPagerContract.View rootView) {
        super(model, rootView);
    }

    public void getSubjects(int subjectType, boolean clear) {
        mModel.getSubjectList(subjectType,clear)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new ErrorHandleSubscriber<SubjectsEntity>(mErrorHandler) {
                    @Override
                    public void onNext(SubjectsEntity subjectsEntity) {
                        mRootView.hideLoading();
                        mRootView.showHotSubject(subjectsEntity.results,clear);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        mRootView.hideLoading();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
