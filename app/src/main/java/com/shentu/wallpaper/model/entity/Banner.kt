package com.shentu.wallpaper.model.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Banner : Serializable {
    @SerializedName("image_url")
    val imageUrl: String = ""
    val color: String = ""
    val url: String = ""
    val type = 0
    val id = -1
    val title: String = ""
    val desc: String = ""
}
