package com.shentu.wallpaper.mvp.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.shentu.wallpaper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RippleItemView extends RelativeLayout {
    @BindView(R.id.iv_icon)
    ImageView mLeftIcon;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_arrow)
    ImageView mEndArrow;
    @BindView(R.id.view_divider)
    View divider;
    @BindView(R.id.tv_end_value)
    TextView mTvEndValue;

    public RippleItemView(Context context) {
        this(context, null);
    }

    public RippleItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RippleItemView);
        int leftIconId = ta.getResourceId(R.styleable.RippleItemView_riv_icon, -1);
        String title = ta.getString(R.styleable.RippleItemView_riv_title);
        boolean isVisibleArrow = ta.getBoolean(R.styleable.RippleItemView_riv_arrow_visible, true);
        boolean isVisibleDivider = ta.getBoolean(R.styleable.RippleItemView_riv_divider_visible, true);
        int iconTint = ta.getColor(R.styleable.RippleItemView_riv_icon_tint,
                ContextCompat.getColor(context, R.color.colorAccent));
        ta.recycle();
        LayoutInflater.from(context).inflate(R.layout.layout_ripple_item, this, true);
        ButterKnife.bind(this);
        if (leftIconId != -1) {
            mLeftIcon.setImageTintList(ColorStateList.valueOf(iconTint));
            mLeftIcon.setImageResource(leftIconId);
        } else {
            mLeftIcon.setVisibility(GONE);
        }
        mTvTitle.setText(title);
        mEndArrow.setVisibility(isVisibleArrow ? VISIBLE : INVISIBLE);
        divider.setVisibility(isVisibleDivider ? VISIBLE : INVISIBLE);
    }

    public void setEndValue(String value) {
        mTvEndValue.setText(value);
    }
}
