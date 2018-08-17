package com.shentu.wallpager.mvp.model.entity

open class BaseKtPageJson<T>{
    var count: Int = 0
    var next: String? = null
    var previous: Any? = null
    var results: ArrayList<T>?=null
}