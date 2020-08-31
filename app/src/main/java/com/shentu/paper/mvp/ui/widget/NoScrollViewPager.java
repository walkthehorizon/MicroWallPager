package com.shentu.paper.mvp.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
/**
 * 该ViewPager可以彻底拦截滑动操作
 * */
public class NoScrollViewPager extends ViewPager {
    boolean canScroll;

    public NoScrollViewPager(@NonNull Context context) {
        this(context,null);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setCanScroll(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canScroll && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return  canScroll && super.onTouchEvent(ev);
    }

    public void setCanScroll(boolean canScroll){
        this.canScroll = canScroll;
    }
}
