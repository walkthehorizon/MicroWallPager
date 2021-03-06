package com.shentu.paper.model.entity

import com.shentu.paper.model.response.BaseResponse

class SplashAd : BaseResponse<Any?>() {
    /**
     * aid : 1
     * cover_url : http://
     * link_url : http://
     */
    var aid: String? = null
    var cover_url: String? = null
    var link_url: String? = null
    var duration //s
            = 0
}