package com.shentu.paper.mvp.ui.widget

import android.text.Editable
import android.text.TextWatcher

abstract class AbsTextWatcher :TextWatcher{
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }


    override fun afterTextChanged(s: Editable?) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}