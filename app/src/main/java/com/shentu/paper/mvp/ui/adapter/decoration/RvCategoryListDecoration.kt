package com.shentu.paper.mvp.ui.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.Dimension
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils

class RvCategoryListDecoration(@Dimension interval: Int) : RecyclerView.ItemDecoration() {
    private val interval: Int = ConvertUtils.dp2px(interval.toFloat())
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildLayoutPosition(view)
        if (pos % 3 == 0) {
            outRect.left = 0
            outRect.top = interval / 2
            outRect.right = interval / 2
            outRect.bottom = interval / 2
            if (pos == 0) {
                outRect.top = 0
            }
            if (parent.childCount - pos == 2) {
                outRect.bottom = 0
            }
        }
        if (pos % 3 == 1) {
            outRect.left = interval / 2
            outRect.top = interval / 2
            outRect.right = interval / 2
            outRect.bottom = interval / 2
            if (pos == 1) {
                outRect.top = 0
            }
            if (parent.childCount - pos == 1) {
                outRect.bottom = 0
            }
        }
        if (pos % 3 == 2) {
            outRect.left = interval / 2
            outRect.top = interval / 2
            outRect.right = 0
            outRect.bottom = interval / 2
            if (pos == 2) {
                outRect.top = 0
            }
            if (parent.childCount - pos == 0) {
                outRect.bottom = 0
            }
        }
    }

}