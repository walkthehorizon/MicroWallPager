package com.shentu.paper.mvp.contract

import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView

interface SplashContract {
    interface View : IView {
        fun toMainPage()
        fun startCountDown(total: Int)
        fun showCountDownText(time: Int)
    }

    interface Model : IModel {

    }
}