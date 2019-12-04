package com.shentu.wallpaper.model.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Banner(val type: Int = 0, val color: String = "") : Serializable {
    @SerializedName("image_url")
    val imageUrl: String = ""
    val url: String = ""
    val id = -1
    val title: String = ""
    val desc: String = ""
}
