package com.shentu.wallpaper.model.entity

import com.google.gson.annotations.SerializedName

class Banner {
    @SerializedName("image_url")
    val imageUrl: String = ""
    @SerializedName("subject_id")
    val subjectId = -1
    val color: String = ""
    val url: String = ""
    val type = 0
    val id = -1
}
