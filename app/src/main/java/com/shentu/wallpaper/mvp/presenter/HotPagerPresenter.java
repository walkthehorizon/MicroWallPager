package com.shentu.wallpaper.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.shentu.wallpaper.app.utils.RxUtils;
import com.shentu.wallpaper.model.entity.BasePageResponse;
import com.shentu.wallpaper.model.entity.BaseResponse;
import com.shentu.wallpaper.model.entity.Subject;
import com.shentu.wallpaper.mvp.contract.HotPagerContract;

import javax.inject.Inject;

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
        mModel.getSubjects(subjectType, clear)
                .compose(RxUtils.applySchedulers(mRootView,clear))
                .subscribe(new ErrorHandleSubscriber<BaseResponse<BasePageResponse<Subject>>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseResponse<BasePageResponse<Subject>> response) {
                        mRootView.showHotSubject(response.getData().getContent(), clear);
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
