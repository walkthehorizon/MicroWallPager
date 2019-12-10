package com.shentu.wallpaper.app.page

import android.content.Context
import android.view.View
import com.kingja.loadsir.callback.Callback
import com.shentu.wallpaper.R

class LoadingCallback : Callback() {
    override fun onCreateView(): Int {
        return R.layout.layout_default_loading
    }

    override fun getSuccessVisible(): Boolean {
        return super.getSuccessVisible()
    }

    override fun onReloadEvent(context: Context, view: View): Boolean {
        return true
    }
}