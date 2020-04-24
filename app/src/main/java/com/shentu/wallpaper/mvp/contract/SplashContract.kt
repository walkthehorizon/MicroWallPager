package com.shentu.wallpaper.mvp.contract

import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.shentu.wallpaper.model.entity.MicroUser
import com.shentu.wallpaper.model.entity.SplashAd
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.model.response.SplashAdResponse
import io.reactivex.Observable

interface SplashContract {
    interface View : IView {
        fun toMainPage()
        fun startCountDown(total: Int)
        fun showCountDownText(time: Int)
    }

    interface Model : IModel {

    }
}