package com.shentu.wallpaper.model.entity

import com.google.gson.annotations.SerializedName

class MicroUser {
    @SerializedName("id")
    var uid: Int = -1
    var nickname: String? = null
    var avatar: String? = null
    var phone: String? = null
    var email: String? = null
    var signature: String? = null
    var date_joined: String? = null
    var last_login: String? = null
    var sex : Int = 0
    var pea = 0
    var token = ""
    override fun toString(): String {
        return "uid:" + uid + "nickname:" + nickname + "avatar:" + avatar + "phone:" + phone + "email:" + email
    }
}