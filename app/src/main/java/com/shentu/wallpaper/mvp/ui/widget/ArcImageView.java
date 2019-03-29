package com.shentu.wallpaper.mvp.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import com.shentu.wallpaper.R;

import androidx.appcompat.widget.AppCompatImageView;

public class ArcImageView extends AppCompatImageView {

    private Path path;
    private int mArcHeight;

    public ArcImageView(Context context) {
        this(context, null);
    }

    public ArcImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcImageView);
        mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcImageView_arc_height, 0);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        path = new Path();
        path.moveTo(0, 0);
        path.lineTo(0, h - mArcHeight);
        path.quadTo(w / 2.0f, h + mArcHeight, w, h - mArcHeight);
        path.lineTo(w, 0);
        path.close();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
