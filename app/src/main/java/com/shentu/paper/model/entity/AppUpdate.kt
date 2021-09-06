package com.shentu.paper.model.entity

import java.io.Serializable

data class AppUpdate  (
    val appUrl: String = "",
    val updateInfo: String="",
    val isForce: Boolean = false,
    val versionCode: Int = 0,
    val versionName: String = "",
    var isUser:Boolean = false
):Serializable