package com.shentu.paper.app.page

import android.content.Context
import android.view.View
import com.kingja.loadsir.callback.Callback
import com.shentu.paper.R

class LoadingCallback : Callback() {
    override fun onCreateView(): Int {
        return R.layout.layout_default_loading
    }

    override fun onReloadEvent(context: Context, view: View): Boolean {
        return true
    }
}