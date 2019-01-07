package com.shentu.wallpaper.mvp.contract

import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.shentu.wallpaper.model.entity.BasePageResponse
import com.shentu.wallpaper.model.entity.BaseResponse
import com.shentu.wallpaper.model.entity.Wallpaper
import io.reactivex.Observable


interface PictureBrowserContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView{
        fun showPictures(pictures: MutableList<Wallpaper>?)

        fun showNavigation()

        fun hideNavigation()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel{
        fun getWallPapersBySubjectId(id:Int):Observable<BaseResponse<BasePageResponse<Wallpaper>>>
    }

}
