package com.shentu.paper.model.entity

import java.io.Serializable

class AppUpdate : Serializable {
    var appUrl: String? = null
    var updateInfo: String? = null
    var isForce: Boolean = false
    var versionCode: Int = 0
    var versionName: String? = null
}
