package com.shentu.wallpaper.mvp.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
public class OnGlideScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        switch (newState){
            case SCROLL_STATE_IDLE:
                //滑动停止
                Glide.with(recyclerView.getContext()).resumeRequests();
                break;
            case SCROLL_STATE_DRAGGING:
                //正在滚动
                Glide.with(recyclerView.getContext()).pauseRequests();
                break;

        }
    }
}
