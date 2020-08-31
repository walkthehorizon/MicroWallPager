package com.shentu.paper.model.entity

import java.io.Serializable

/**
 * @param type 1、打开banner列表
 * */
class Banner(val type: Int = 0, val color: String = "") : Serializable {
    val imageUrl: String = ""
    val url: String = ""
    val id = -1
    val title: String = ""
    val desc: String = ""
}
