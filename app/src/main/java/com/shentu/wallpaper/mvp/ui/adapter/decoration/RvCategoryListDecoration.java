package com.shentu.wallpaper.mvp.ui.adapter.decoration;

import android.graphics.Rect;
import androidx.annotation.Dimension;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;

public class RvCategoryListDecoration extends RecyclerView.ItemDecoration{

    private int interval;

    public RvCategoryListDecoration(@Dimension int interval) {
        this.interval = ConvertUtils.dp2px(interval);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildLayoutPosition(view);
        if(pos %3 ==0){
            outRect.left = 0;
            outRect.top = interval/2;
            outRect.right = interval/2;
            outRect.bottom = interval/2;
            if (pos == 0) {
                outRect.top = 0;
            }
            if (parent.getChildCount() - pos == 2) {
                outRect.bottom = 0;
            }
        }
        if(pos %3 ==1){
            outRect.left = interval/2;
            outRect.top = interval/2;
            outRect.right = interval/2;
            outRect.bottom = interval/2;
            if (pos == 1) {
                outRect.top = 0;
            }
            if (parent.getChildCount() - pos == 1) {
                outRect.bottom = 0;
            }
        }
        if(pos %3 ==2){
            outRect.left = interval/2;
            outRect.top = interval/2;
            outRect.right = 0;
            outRect.bottom = interval/2;
            if (pos == 2) {
                outRect.top = 0;
            }
            if (parent.getChildCount() - pos == 0) {
                outRect.bottom = 0;
            }
        }
    }
}
