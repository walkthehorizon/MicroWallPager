package com.shentu.paper.mvp.contract

import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IView
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.WallpaperPageResponse
import io.reactivex.Observable


interface HomeRankContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        fun showRanks(papers: MutableList<Wallpaper>, clear: Boolean)
    }

}
