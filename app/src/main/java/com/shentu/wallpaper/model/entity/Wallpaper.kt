package com.shentu.wallpaper.model.entity

import java.io.Serializable

class Wallpaper : Serializable {

    var id: Int = 0
    var url: String? = null
    var origin_url: String? = null
    var sw: Int = 0
    var sh: Int = 0

    /**
     * 判断原图是否存在
     */
    var isOriginExist: Boolean = false

}