package com.shentu.paper.model.entity

import java.io.Serializable

data class Wallpaper(
    var url: String = "",
    var id: Long = -1,
    var originUrl: String = "",
    var subjectId :Int = -1,
    var categoryId:Int = -1,
    var collectNum:Int = 0,
    var shareNum:Int = 0,
    var downloadNum:Int = 0,
    var commentNum:Int = 0,
    var created:String = "",
    /**
     * 判断原图是否存在
     */
    var isOriginExist: Boolean = false,
    var collected:Boolean = false,
    //delete
    var checked :Boolean= false,
    /**
     * from subject
     * */
    var description: String = "",
    var title: String = "",
) : Serializable
