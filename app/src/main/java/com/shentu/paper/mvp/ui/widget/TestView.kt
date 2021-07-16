package com.shentu.paper.mvp.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 *    date   : 2021/7/6 17:09
 *    author : mingming.li
 *    e-mail : mingming.li@ximalaya.com
 */
class TestView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getTestDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            getTestDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        )
    }

    private fun getTestDefaultSize(size: Int, measureSpec: Int): Int {
        var result = size
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.UNSPECIFIED -> result = size
            MeasureSpec.AT_MOST -> result = 100
            MeasureSpec.EXACTLY -> result = specSize
        }
        return result
    }
}