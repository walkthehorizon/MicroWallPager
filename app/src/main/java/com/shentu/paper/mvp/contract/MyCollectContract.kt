package com.shentu.paper.mvp.contract

import com.micro.mvp.IModel
import com.micro.mvp.IView
import com.shentu.paper.model.body.DelCollectBody
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import io.reactivex.Observable


interface MyCollectContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun openDelMode()

        fun closeDelMode(removeData: Boolean = false)

        fun showCollects(wallpapers: List<Wallpaper>, clear: Boolean)

        fun showDelDialog()

        fun showProgress()

        fun hideProgress()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun getMyCollects(clear: Boolean): Observable<WallpaperPageResponse>

        fun delCollects(body: DelCollectBody): Observable<BaseResponse<Boolean>>
    }

}
