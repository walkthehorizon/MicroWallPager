package com.shentu.paper.mvp.contract

import com.micro.mvp.IModel
import com.micro.mvp.IView

interface SplashContract {
    interface View : IView {
        fun toMainPage()
        fun startCountDown(total: Int)
        fun showCountDownText(time: Int)
    }

    interface Model : IModel {

    }
}