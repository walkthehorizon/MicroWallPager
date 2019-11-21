package com.shentu.wallpaper.model.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Wallpaper(var url: String = "") : Serializable{
    var id: Int = 0
    @SerializedName("origin_url")
    var originUrl: String = ""
    @SerializedName("subject_id")
    var subjectId = 0
    @SerializedName("collect_num")
    var collectNum = 0
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

    var normalPrice = 1

    var originPrice = 3
}
