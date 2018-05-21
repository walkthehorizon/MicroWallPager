package com.shentu.wallpager.mvp.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shentu.wallpager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DefaultToolbar extends RelativeLayout {
    @BindView(R.id.iv_start)
    ImageView mIvStart;
    @BindView(R.id.iv_end)
    ImageView mIvEnd;
    @BindView(R.id.toolbar_title)
    TextView mTvTitle;

    public DefaultToolbar(Context context) {
        this(context, null);
    }

    public DefaultToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.default_toolbar, this, true);
        ButterKnife.bind(this);
    }

    public void setBackVisible(boolean visible) {
        mIvStart.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setTitle(String text) {
        mTvTitle.setText(text);
    }
}
