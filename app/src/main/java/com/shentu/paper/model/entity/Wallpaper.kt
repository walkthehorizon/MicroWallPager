package com.shentu.paper.model.entity

import java.io.Serializable

class Wallpaper(var url: String = "") : Serializable {
    var id: Int = -1
    var originUrl: String = ""
    var subjectId = -1
    var categoryId = -1
    var collectNum = 0
    var shareNum = 0
    var downloadNum = 0
    var commentNum = 0

    var created = ""
    /**
     * 判断原图是否存在
     */
    var isOriginExist: Boolean = false
    var collected = false

    //delete
    var checked = false

    /**
     * from subject
     * */
    var description: String = ""

    var title: String = ""

}
