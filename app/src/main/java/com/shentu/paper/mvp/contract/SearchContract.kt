package com.shentu.paper.mvp.contract

import com.micro.mvp.IModel
import com.micro.mvp.IView
import com.shentu.paper.app.utils.LimitQueue
import com.shentu.paper.model.entity.Subject
import com.shentu.paper.model.response.SubjectPageResponse
import io.reactivex.Observable


interface SearchContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun showResults(subjects: MutableList<Subject>, clear: Boolean)

        fun showHistory(queue:LimitQueue<String>)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun searchKey(key: String, clear: Boolean): Observable<SubjectPageResponse>
    }

}
