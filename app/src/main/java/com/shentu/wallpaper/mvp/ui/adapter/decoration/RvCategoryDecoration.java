package com.shentu.wallpaper.mvp.ui.adapter.decoration;

import android.graphics.Rect;
import android.support.annotation.Dimension;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;

public class RvCategoryDecoration extends RecyclerView.ItemDecoration {
    private int interval;

    public RvCategoryDecoration(@Dimension int interval) {
        this.interval = ConvertUtils.dp2px(interval);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildLayoutPosition(view);
        if (pos % 3 == 0) {
            outRect.left = interval;
            outRect.right = interval / 2;
            outRect.top = interval / 2;
            outRect.bottom = interval / 2;
            if (pos == 0) {
                outRect.top = interval;
            }
            if (parent.getChildCount() - pos == 2) {
                outRect.bottom = interval;
            }
        }
        if (pos % 3 == 1) {
            outRect.left = interval / 2;
            outRect.right = interval / 2;
            outRect.top = interval / 2;
            outRect.bottom = interval / 2;
            if (pos == 1) {
                outRect.top = interval;
            }
            if (parent.getChildCount() - pos == 1) {
                outRect.bottom = interval;
            }
        }
        if (pos % 3 == 2) {
            outRect.left = interval / 2;
            outRect.right = interval;
            outRect.top = interval / 2;
            outRect.bottom = interval / 2;
            if (pos == 2) {
                outRect.top = interval;
            }
            if (parent.getChildCount() - pos == 0) {
                outRect.bottom = interval;
            }
        }
    }
}
