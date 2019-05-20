package com.shentu.wallpaper.mvp.ui.adapter.decoration

import android.graphics.Rect
import android.view.View

import androidx.annotation.Dimension
import androidx.recyclerview.widget.RecyclerView

import com.blankj.utilcode.util.ConvertUtils

class RvCategoryDecoration(@Dimension interval: Int) : RecyclerView.ItemDecoration() {
    private val interval: Int = ConvertUtils.dp2px(interval.toFloat())

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildLayoutPosition(view)
        if (pos % 2 == 0) {
            outRect.left = interval
            outRect.right = interval / 2
            outRect.top = if (pos == 0) 0 else interval / 2
            outRect.bottom = if (parent.childCount - pos <= 2) 0 else interval / 2
        } else {
            outRect.left = interval / 2
            outRect.right = interval
            outRect.top = if (pos == 1) 0 else interval / 2
            outRect.bottom = if (parent.childCount - pos <= 1) 0 else interval / 2
        }
    }
}
