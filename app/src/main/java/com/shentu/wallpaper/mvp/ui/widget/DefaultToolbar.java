package com.shentu.wallpaper.mvp.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shentu.wallpaper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DefaultToolbar extends RelativeLayout implements View.OnClickListener{
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
        mIvEnd.setOnClickListener(this);
    }

    public void setBackVisible(boolean visible) {
        mIvStart.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setTitle(String text) {
        mTvTitle.setText(text);
    }

    public void setRightIcon(int icon){
        mIvEnd.setImageResource(icon);
        mIvEnd.setVisibility(VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if(listener == null){
            return;
        }
        switch (view.getId()){
            case R.id.iv_end:  listener.onClickRightIcon();break;
        }
    }

    private OnClickListener listener;

    interface OnClickListener{
        void onClickRightIcon();
    }

    public abstract static class OnClickListenerImpl implements OnClickListener{

        @Override
        public void onClickRightIcon() {

        }
    }

    public void setOnClickListener(OnClickListenerImpl listener){
        this.listener = listener;
    }
}
