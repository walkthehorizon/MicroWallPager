package com.shentu.wallpaper.mvp.ui.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import me.relex.circleindicator.CircleIndicator2;

public class LooperCircleIndicator extends CircleIndicator2 {
    private ViewPager2 mViewpager;

    public LooperCircleIndicator(Context context) {
        super(context);
    }

    public LooperCircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LooperCircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LooperCircleIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setViewPager(@Nullable ViewPager2 viewPager) {
        mViewpager = viewPager;
        if (mViewpager != null && mViewpager.getAdapter() != null) {
            mLastPosition = -1;
            createIndicators();
            mViewpager.unregisterOnPageChangeCallback(mInternalPageChangeListener);
            mViewpager.registerOnPageChangeCallback(mInternalPageChangeListener);
            mInternalPageChangeListener.onPageSelected(mViewpager.getCurrentItem());
        }
    }

    private void createIndicators() {
        removeAllViews();
        RecyclerView.Adapter adapter = mViewpager.getAdapter();
        int count;
        if (adapter == null || (count = adapter.getItemCount()) <= 0) {
            return;
        }
        createIndicators(count, mViewpager.getCurrentItem());
    }

    private final ViewPager2.OnPageChangeCallback mInternalPageChangeListener =
            new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    if (mViewpager.getAdapter() == null
                            || mViewpager.getAdapter().getItemCount() <= 0) {
                        return;
                    }
                    internalPageSelected(position);
                    mLastPosition = position;
                }
            };

    public DataSetObserver getDataSetObserver() {
        return mInternalDataSetObserver;
    }

    private final DataSetObserver mInternalDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mViewpager == null) {
                return;
            }
            RecyclerView.Adapter adapter = mViewpager.getAdapter();
            int newCount = adapter != null ? adapter.getItemCount() : 0;
            int currentCount = getChildCount();
            if (newCount == currentCount) {
                // No change
                return;
            } else if (mLastPosition < newCount) {
                mLastPosition = mViewpager.getCurrentItem();
            } else {
                mLastPosition = -1;
            }
            createIndicators();
        }
    };

}
