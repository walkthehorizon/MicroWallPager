package com.shentu.paper.mvp.contract

import com.micro.mvp.IModel
import com.micro.mvp.IView
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.model.response.BaseResponse
import io.reactivex.Observable


interface LoginContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView{
        fun showVerifyDialog()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun registerAccount(phone: String,password: String): Observable<BaseResponse<Boolean>>

        fun loginAccount(phone: String): Observable<BaseResponse<MicroUser>>
    }

}
