package com.shentu.wallpaper.mvp.ui.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.Dimension
import androidx.recyclerview.widget.RecyclerView

class CollectListDecoration(@Dimension val offset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(offset / 2, offset / 2, offset / 2, offset / 2)
        val pos = parent.getChildLayoutPosition(view)
        if (pos % 3 == 0) {
            outRect.left = offset
        }
        if (pos % 3 == 2) {
            outRect.right = offset
        }
    }
}