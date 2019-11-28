package com.shentu.wallpaper.mvp.contract

import android.widget.EditText
import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.shentu.wallpaper.app.utils.LimitQueue
import com.shentu.wallpaper.model.entity.Subject
import com.shentu.wallpaper.model.response.SubjectPageResponse
import io.reactivex.Observable


interface SearchContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun showResults(subjects: MutableList<Subject>, clear: Boolean)

        fun showHistory(queue:LimitQueue<String>)

        fun getEtSearch():EditText
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun searchKey(key: String, clear: Boolean): Observable<SubjectPageResponse>
    }

}
