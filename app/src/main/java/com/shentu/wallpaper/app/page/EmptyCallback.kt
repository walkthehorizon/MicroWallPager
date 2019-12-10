package com.shentu.wallpaper.app.page

import com.kingja.loadsir.callback.Callback
import com.shentu.wallpaper.R

class EmptyCallback : Callback {
    private var emptyResId: Int

    constructor(emptyResId: Int) {
        this.emptyResId = emptyResId
    }

    constructor() {
        emptyResId = R.layout.layout_default_empty
    }

    override fun onCreateView(): Int {
        return emptyResId
    }
}