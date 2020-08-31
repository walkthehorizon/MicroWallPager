package com.shentu.paper.model.response

import java.io.Serializable

open class BasePageResponse<T : Any> : Serializable {
    var count: Int = 0
    var next: String? = null
    var previous: String? = null
    var content: MutableList<T> = mutableListOf()
}
