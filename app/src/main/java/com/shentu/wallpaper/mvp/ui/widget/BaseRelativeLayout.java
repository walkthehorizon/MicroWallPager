package com.shentu.wallpaper.mvp.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;

public abstract class BaseRelativeLayout extends RelativeLayout {
    public BaseRelativeLayout(Context context) {
        this(context, null);
    }

    public BaseRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getLayoutId() != -1) {
            LayoutParams lp = new LayoutParams(-2, -2);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(LayoutInflater.from(context).inflate(getLayoutId(), this, false), lp);
            ButterKnife.bind(this);
        }
        initView(context, attrs);
    }

    public int dp2px(final float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int px2dp(final float pxValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public int sp2px(final float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int px2sp(final float pxValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    protected abstract int getLayoutId();

    protected abstract void initView(Context context, AttributeSet attrs);
}
