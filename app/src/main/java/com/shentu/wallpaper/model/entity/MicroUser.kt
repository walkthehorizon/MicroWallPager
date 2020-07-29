package com.shentu.wallpaper.model.entity

data class MicroUser(
        var avatar: String = "",
        var email: String = "",
        var nPrice: Int = 1,
        var nickname: String = "",
        var oPrice: Int = 3,
        var pea: Int = 0,
        var phone: String = "",
        var sex: Int = 0,
        var signature: String = "",
        var token: String = "",
        var uid: Int = 0,
        var vip: Boolean = false,
        var showDonateInterval: Long = 24 * 60 * 60 * 1000//一天
)