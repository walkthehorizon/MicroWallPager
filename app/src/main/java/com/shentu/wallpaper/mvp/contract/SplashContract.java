package com.shentu.wallpaper.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.shentu.wallpaper.model.entity.SplashAd;
import com.shentu.wallpaper.model.response.SplashAdResponse;

import io.reactivex.Observable;

public interface SplashContract {
    interface View extends IView {
        void showSplash(SplashAd splashAd);

        void toMainPage();

        void startCountDown(int total);

        void showCountDownText(int time);
    }

    interface Model extends IModel {
        Observable<SplashAdResponse> getSplashAd();
    }
}
