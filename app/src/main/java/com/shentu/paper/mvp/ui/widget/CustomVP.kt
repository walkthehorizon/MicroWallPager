package com.shentu.paper.mvp.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class CustomVP : ViewGroup {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec,heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var tHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val cWidth = child.measuredWidth
            val cHeight = child.measuredHeight
            child.layout(0, tHeight, cWidth, cHeight + tHeight)
            tHeight += cHeight
        }
    }
}