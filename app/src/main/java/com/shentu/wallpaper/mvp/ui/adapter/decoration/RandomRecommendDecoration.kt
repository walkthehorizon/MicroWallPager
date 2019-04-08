package com.shentu.wallpaper.mvp.ui.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RandomRecommendDecoration(private val decoration: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position: Int = parent.getChildLayoutPosition(view)
        outRect.left = if (position % 2 == 0) decoration else decoration / 2
        outRect.top = if (position == 0 || position == 1) 0 else decoration
        outRect.right = if (position % 2 == 0) decoration / 2 else decoration
    }

}