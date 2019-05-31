package com.shentu.wallpaper.mvp.contract

import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.shentu.wallpaper.model.entity.*
import com.shentu.wallpaper.model.response.BannerPageResponse
import com.shentu.wallpaper.model.response.WallpaperPageResponse

import io.reactivex.Observable


interface TabHomeContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
//        fun showHotSubject(subjects: List<Subject>, clear: Boolean) //展示推荐壁纸

        fun showFilterPop()

        fun showRecommends(wallpapers: MutableList<Wallpaper>, clear: Boolean)

        fun showBanners(banners:MutableList<Banner>)
    }

    interface Model : IModel {
        fun getSubjects(subjectType: Int, clear: Boolean): Observable<BaseResponse<BasePageResponse<Subject>>>

        fun getRecommends(clear: Boolean): Observable<WallpaperPageResponse>

        fun getBanners(): Observable<BannerPageResponse>
    }
}
