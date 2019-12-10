package com.shentu.wallpaper.model.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

class Subject : MultiItemEntity {
    var id = 0
    var name: String=""
    var cover: String = ""
    var description: String = ""
    var tag: String? = null
    var created: String=""
    var bgColor = 0
    var cover_1: String = ""
    var cover_2: String = ""
    var type: Int
    override fun getItemType(): Int {
        return type
    }

    companion object {
        const val ITEM_VIEW_1 = 1 //魅族壁纸
        const val ITEM_VIEW_2 = 2 //cosplay
        const val ITEM_VIEW_3 = 3 //性感美女
    }

    init {
        type = ITEM_VIEW_2
    }
}