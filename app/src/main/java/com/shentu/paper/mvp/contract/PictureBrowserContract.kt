package com.shentu.paper.mvp.contract

import com.micro.mvp.IModel
import com.micro.mvp.IView
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.ui.browser.SaveType
import io.reactivex.Observable


interface PictureBrowserContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun showPictures(pictures: MutableList<Wallpaper>)

        fun showNavigation()

        fun hideNavigation()

        fun setWallpaper(path: String)

        fun showLikeStatus(position: Int)

        fun savePicture(currentItem: Int, type: SaveType)

        fun resetCollect()

        fun showShare(paper: Wallpaper)

        fun showDonateDialog()

        fun showCommentDialog()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun addCollect(pid: Long): Observable<BaseResponse<Boolean>>

        fun getWallPapersBySubjectId(id: Int): Observable<WallpaperPageResponse>

        fun updateCategoryCover(cid: Int, logo: String): Observable<BaseResponse<Boolean>>

        fun buyPaper(pk: Long, pea: Int): Observable<BaseResponse<Int>>

        fun getPaperDetail(pk: Long): Observable<BaseResponse<Wallpaper>>

        fun setGarbage(paperId: Long):Observable<BaseResponse<String>>

        fun addPaper2Banner(bid: Int, pid: Long): Observable<BaseResponse<Boolean>>
    }

}
