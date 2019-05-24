package com.shentu.wallpaper.mvp.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shentu.wallpaper.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DefaultToolbar extends BaseRelativeLayout implements View.OnClickListener {
    @BindView(R.id.iv_start)
    ImageView mIvStart;
    @BindView(R.id.iv_end)
    ImageView mIvEnd;
    @BindView(R.id.toolbar_title)
    TextView mTvTitle;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.ivEnd2)
    ImageView mIvEnd2;

    private String tlTitle;
    private int leftIcon;
    private int rightIcon;
    private int rightIcon2;
    private String rightText;
    private float endTextSize;
    private int startIconColor;


    public DefaultToolbar(Context context) {
        this(context, null);
    }

    public DefaultToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(LayoutInflater.from(context).inflate(R.layout.default_toolbar, this, false), lp);
        ButterKnife.bind(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DefaultToolbar);
        tlTitle = a.getString(R.styleable.DefaultToolbar_tl_title);
        leftIcon = a.getResourceId(R.styleable.DefaultToolbar_tl_left_icon, R.drawable.ic_arrow_back_white_24dp);
        rightIcon = a.getResourceId(R.styleable.DefaultToolbar_tl_right_icon, -1);
        rightText = a.getString(R.styleable.DefaultToolbar_tl_right_text);
        rightIcon2 = a.getResourceId(R.styleable.DefaultToolbar_tl_right_icon2, -1);
        endTextSize = a.getDimension(R.styleable.DefaultToolbar_tl_end_text_size, 16);
        startIconColor = a.getColor(R.styleable.DefaultToolbar_tl_left_tint, Color.WHITE);
        a.recycle();

        setTitle(tlTitle);
        setLeftIcon(leftIcon);
        setRightIcon(rightIcon);
        setRightText(rightText);
        setRightIcon2(rightIcon2);
    }

    @Override
    protected int getLayoutId() {
        return -1;
    }

    @Override
    protected void initView(Context context, AttributeSet attrs) {

    }

    public ImageView getLeftIconView() {
        return mIvStart;
    }

    public int getLeftIcon() {
        return leftIcon;
    }

    public void setRightIcon2(int icon) {
        if (icon != -1) {
            mIvEnd2.setVisibility(VISIBLE);
            mIvEnd2.setImageResource(icon);
            mIvEnd2.setOnClickListener(this);
        }
    }

    private void setLeftIcon(int icon) {
        if (icon != -1) {
            mIvStart.setVisibility(VISIBLE);
            mIvStart.setImageTintList(ColorStateList.valueOf(startIconColor));
            mIvStart.setImageResource(icon);
            mIvStart.setOnClickListener(this);
        }
    }

    public void setRightIcon(int icon) {
        if (icon != -1) {
            mIvEnd.setVisibility(VISIBLE);
            mIvEnd.setImageResource(icon);
            mIvEnd.setOnClickListener(this);
        }
    }

    public void setRightText(String str) {
        if (!TextUtils.isEmpty(str)) {
            tvRight.setVisibility(VISIBLE);
            tvRight.setText(str);
            tvRight.setTextSize(endTextSize);
            tvRight.setOnClickListener(this);
        }
    }

    public ImageView getRightIconView() {
        return mIvEnd;
    }

    public void setTitle(@Nullable String text) {
        mTvTitle.setText(text == null ? "" : text);
    }

    @Override
    public void onClick(View view) {
        if (listeners == null) {
            return;
        }

        for (int i = 0; i < listeners.size(); i++) {
            switch (view.getId()) {
                case R.id.iv_end:
                    listeners.get(i).onClickRightIcon();
                    break;
                case R.id.iv_start:
                    listeners.get(i).onClickLeftIcon();
                    break;
                case R.id.tvRight:
                    listeners.get(i).clickRightText();
                    break;
                case R.id.ivEnd2:
                    listeners.get(i).onClickRightIcon2();
                    break;
            }
        }
    }

    private List<OnClickListener> listeners;

    public interface OnClickListener {
        default void onClickLeftIcon() {

        }

        default void onClickRightIcon() {

        }

        default void onClickRightIcon2() {

        }

        default void clickRightText() {

        }
    }

    public void addOnClickListener(OnClickListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        if (listener == null) {
            return;
        }
        listeners.add(listener);
    }
}
