package com.shentu.paper.app.page

import com.kingja.loadsir.callback.Callback
import com.shentu.paper.R

class ErrorCallback : Callback() {
    override fun onCreateView(): Int {
        return R.layout.layout_default_error
    }
}