package com.shentu.wallpaper.mvp.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        this(context,null);
    }

    public RippleItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RippleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.RippleItemView);
        int leftIconId = ta.getResourceId(R.styleable.RippleItemView_item_icon, -1);
        String title = ta.getString(R.styleable.RippleItemView_item_title);
        Boolean isVisibleArrow = ta.getBoolean(R.styleable.RippleItemView_item_arrow_visible, true);
        Boolean isVisibleDivider = ta.getBoolean(R.styleable.RippleItemView_item_divider_visible,true);
        ta.recycle();
        LayoutInflater.from(context).inflate(R.layout.layout_ripple_item, this, true);
        ButterKnife.bind(this);
        mLeftIcon.setImageResource(leftIconId);
        mTvTitle.setText(title);
        mEndArrow.setVisibility(isVisibleArrow ?VISIBLE:INVISIBLE);
        divider.setVisibility(isVisibleDivider?VISIBLE:INVISIBLE);
    }

    public void setEndValue(String value){
        mTvEndValue.setText(value);
        mEndArrow.setVisibility(GONE);
    }
}
