package com.shentu.wallpaper.model.entity

import java.io.Serializable

open class BasePageResponse<T> : Serializable {
    var count: Int = 0
    var next: String? = null
    var previous: Any? = null
    var content: MutableList<T>? = null
}
