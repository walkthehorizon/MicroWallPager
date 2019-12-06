package com.shentu.wallpaper.mvp.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.shentu.wallpaper.R

class ArcImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var path: Path? = null
    private val mArcHeight: Int
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path = Path()
        path!!.moveTo(0f, 0f)
        path!!.lineTo(0f, h - mArcHeight.toFloat())
        path!!.quadTo(w / 2.0f, h + mArcHeight.toFloat(), w.toFloat(), h - mArcHeight.toFloat())
        path!!.lineTo(w.toFloat(), 0f)
        path!!.close()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(path)
        super.onDraw(canvas)
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcImageView)
        mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcImageView_arc_height, 0)
        typedArray.recycle()
    }
}