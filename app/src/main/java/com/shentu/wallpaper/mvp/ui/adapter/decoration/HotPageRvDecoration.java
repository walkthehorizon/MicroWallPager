package com.shentu.wallpaper.mvp.ui.adapter.decoration;

import android.graphics.Rect;
import android.support.annotation.Dimension;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;

public class HotPageRvDecoration extends RecyclerView.ItemDecoration {
    private int offset;

    public HotPageRvDecoration(@Dimension int offset) {
        this.offset = ConvertUtils.dp2px(offset);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = 0;
        }else {
            outRect.top = offset;
        }
    }
}
