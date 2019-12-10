package com.shentu.wallpaper.app.page

import com.kingja.loadsir.callback.Callback
import com.shentu.wallpaper.R

class ErrorCallback : Callback() {
    override fun onCreateView(): Int {
        return R.layout.layout_default_error
    }
}