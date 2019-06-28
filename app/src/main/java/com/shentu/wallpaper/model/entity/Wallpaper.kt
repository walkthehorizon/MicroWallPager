package com.shentu.wallpaper.model.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Wallpaper(var url: String = "") : Serializable {

    var id: Int = 0
    @SerializedName("origin_url")
    var originUrl: String = ""
    var sw: Int = 0
    var sh: Int = 0

    /**
     * 判断原图是否存在
     */
    var isOriginExist: Boolean = false


}
