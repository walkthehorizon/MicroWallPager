package com.shentu.wallpager.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;

import io.reactivex.Observable;
import me.jessyan.mvparms.demo.mvp.model.entity.SplashAd;

public interface SplashContract {
    interface View extends IView {
        void showSplash(SplashAd splashAd);
    }

    interface Model extends IModel {
        Observable<SplashAd> getSplashAd();
    }
}
