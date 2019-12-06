package com.shentu.wallpaper.mvp.ui.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.Dimension
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils

class HotPageRvDecoration(@Dimension offset: Int) : RecyclerView.ItemDecoration() {
    private val offset: Int = ConvertUtils.dp2px(offset.toFloat())
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = 0
        } else {
            outRect.top = offset
        }
    }

}