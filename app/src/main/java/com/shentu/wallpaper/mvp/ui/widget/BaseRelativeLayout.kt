package com.shentu.wallpaper.mvp.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import butterknife.ButterKnife

abstract class BaseRelativeLayout @JvmOverloads constructor(context: Context , attrs: AttributeSet?,defStyleAttr:Int=-1) :RelativeLayout(context,attrs,defStyleAttr){

    fun dp2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dp(pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun px2sp(pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }
}