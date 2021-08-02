package com.shentu.paper.mvp.contract

import com.micro.mvp.IModel
import com.micro.mvp.IView
import com.shentu.paper.model.entity.Wallpaper


interface CategoryPageContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView{
        fun showCategoryPicture(wallpapers:MutableList<Wallpaper>)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel{
//        fun getCategoryList(id: Int, page: Int, clear: Boolean): Observable<CategoryListEntity>
    }

}
