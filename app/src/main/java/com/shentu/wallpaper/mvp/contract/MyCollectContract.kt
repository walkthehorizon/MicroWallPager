package com.shentu.wallpaper.mvp.contract

import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.shentu.wallpaper.model.entity.BaseResponse
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import io.reactivex.Observable


interface MyCollectContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun showCollects(wallpapers: List<Wallpaper>, clear: Boolean)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun addCollect(pid: Int): Observable<BaseResponse<Boolean>>

        fun getMyCollects(clear: Boolean): Observable<WallpaperPageResponse>
    }

}
