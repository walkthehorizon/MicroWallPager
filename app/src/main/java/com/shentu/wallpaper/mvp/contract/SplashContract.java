package com.shentu.wallpaper.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.shentu.wallpaper.model.entity.SplashAd;


import io.reactivex.Observable;

public interface SplashContract {
    interface View extends IView {
        void showSplash(SplashAd splashAd);
    }

    interface Model extends IModel {
        Observable getSplashAd();
    }
}
