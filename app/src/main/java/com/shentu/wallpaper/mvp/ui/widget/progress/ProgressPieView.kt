package com.shentu.wallpaper.mvp.ui.widget.progress

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.LruCache
import android.view.View
import androidx.core.content.ContextCompat
import com.shentu.wallpaper.R

class ProgressPieView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    interface OnProgressListener {
        fun onProgressChanged(progress: Int, max: Int)
        fun onProgressCompleted()
    }

    private var mListener: OnProgressListener? = null
    private lateinit var mDisplayMetrics: DisplayMetrics
    private var mMax = DEFAULT_MAX
    private var mProgress = DEFAULT_PROGRESS
    /**
     * Gets the start angle the [.FILL_TYPE_RADIAL] uses.
     */
    /**
     * Sets the start angle the [.FILL_TYPE_RADIAL] uses.
     *
     * @param startAngle start angle in degrees
     */
    var startAngle = DEFAULT_START_ANGLE
    /**
     * Gets the inverted state.
     */
    /**
     * Sets the inverted state.
     *
     * @param inverted draw the progress inverted or not
     */
    var isInverted = false
    /**
     * Gets the counterclockwise state.
     */
    /**
     * Sets the counterclockwise state.
     *
     * @param counterclockwise draw the progress counterclockwise or not
     */
    var isCounterclockwise = false
    /**
     * Gets the show stroke state.
     */
    var isStrokeShowing = true
        private set
    /**
     * Gets the stroke width in dp.
     */
    var strokeWidth = DEFAULT_STROKE_WIDTH
        private set
    /**
     * Gets the show text state.
     */
    var isTextShowing = true
        private set
    /**
     * Gets the text size in sp.
     */
    var textSize = DEFAULT_TEXT_SIZE
        private set
    private var mText: String? = null
    private var mTypeface: String? = null
    /**
     * Gets the show image state.
     */
    var isImageShowing = true
        private set
    private var mImage: Drawable? = null
    private lateinit var mImageRect: Rect
    private lateinit var mStrokePaint: Paint
    private lateinit var mTextPaint: Paint
    private lateinit var mProgressPaint: Paint
    private lateinit var mBackgroundPaint: Paint
    private lateinit var mInnerRectF: RectF
    /**
     * Gets the progress fill type.
     */
    /**
     * Sets the progress fill type.
     *
     * @param fillType one of [.FILL_TYPE_CENTER], [.FILL_TYPE_RADIAL]
     */
    var progressFillType = FILL_TYPE_RADIAL
    /**
     * Returns the current animation speed used in animateProgressFill method.
     */
    /**
     * Sets the animation speed used in the animateProgressFill method.
     */
    var animationSpeed = MEDIUM_ANIMATION_SPEED
    private val mAnimationHandler = AnimationHandler()
    private var mViewSize = 0
    private fun init(context: Context, attrs: AttributeSet?) {
        mDisplayMetrics = context.resources.displayMetrics
        strokeWidth = (strokeWidth * mDisplayMetrics.density)
        textSize = (textSize * mDisplayMetrics.scaledDensity)
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProgressPieView)
        val res = resources
        mMax = a.getInteger(R.styleable.ProgressPieView_ppvMax, mMax)
        mProgress = a.getInteger(R.styleable.ProgressPieView_ppvProgress, mProgress)
        startAngle = a.getInt(R.styleable.ProgressPieView_ppvStartAngle, startAngle)
        isInverted = a.getBoolean(R.styleable.ProgressPieView_ppvInverted, isInverted)
        isCounterclockwise = a.getBoolean(R.styleable.ProgressPieView_ppvCounterclockwise, isCounterclockwise)
        strokeWidth = a.getDimension(R.styleable.ProgressPieView_ppvStrokeWidth, strokeWidth)
        mTypeface = a.getString(R.styleable.ProgressPieView_ppvTypeface)
        textSize = a.getDimension(R.styleable.ProgressPieView_android_textSize, textSize)
        mText = a.getString(R.styleable.ProgressPieView_android_text)
        isStrokeShowing = a.getBoolean(R.styleable.ProgressPieView_ppvShowStroke, isStrokeShowing)
        isTextShowing = a.getBoolean(R.styleable.ProgressPieView_ppvShowText, isTextShowing)
        mImage = a.getDrawable(R.styleable.ProgressPieView_ppvImage)
        var backgroundColor = ContextCompat.getColor(context, R.color.default_background_color)
        backgroundColor = a.getColor(R.styleable.ProgressPieView_ppvBackgroundColor, backgroundColor)
        var progressColor = ContextCompat.getColor(context, R.color.default_progress_color)
        progressColor = a.getColor(R.styleable.ProgressPieView_ppvProgressColor, progressColor)
        var strokeColor = ContextCompat.getColor(context, R.color.default_stroke_color)
        strokeColor = a.getColor(R.styleable.ProgressPieView_ppvStrokeColor, strokeColor)
        var textColor = ContextCompat.getColor(context,R.color.default_text_color)
        textColor = a.getColor(R.styleable.ProgressPieView_android_textColor, textColor)
        progressFillType = a.getInteger(R.styleable.ProgressPieView_ppvProgressFillType, progressFillType)
        a.recycle()
        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint.color = backgroundColor
        mBackgroundPaint.style = Paint.Style.FILL
        mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mProgressPaint.color = progressColor
        mProgressPaint.style = Paint.Style.FILL
        mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokePaint.color = strokeColor
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.strokeWidth = strokeWidth
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.color = textColor
        mTextPaint.textSize = textSize
        mTextPaint.textAlign = Paint.Align.CENTER
        mInnerRectF = RectF()
        mImageRect = Rect()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = resolveSize(DEFAULT_VIEW_SIZE, widthMeasureSpec)
        val height = resolveSize(DEFAULT_VIEW_SIZE, heightMeasureSpec)
        mViewSize = Math.min(width, height)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mInnerRectF[0f, 0f, mViewSize.toFloat()] = mViewSize.toFloat()
        mInnerRectF.offset((width - mViewSize) / 2.toFloat(), (height - mViewSize) / 2.toFloat())
        if (isStrokeShowing) {
            val halfBorder = (mStrokePaint.strokeWidth / 2f + 0.5f).toInt()
            mInnerRectF.inset(halfBorder.toFloat(), halfBorder.toFloat())
        }
        val centerX = mInnerRectF.centerX()
        val centerY = mInnerRectF.centerY()
        canvas.drawArc(mInnerRectF, 0f, 360f, true, mBackgroundPaint)
        when (progressFillType) {
            FILL_TYPE_RADIAL -> {
                var sweepAngle = 360 * mProgress / mMax.toFloat()
                if (isInverted) {
                    sweepAngle = sweepAngle - 360
                }
                if (isCounterclockwise) {
                    sweepAngle = -sweepAngle
                }
                canvas.drawArc(mInnerRectF, startAngle.toFloat(), sweepAngle, true, mProgressPaint)
            }
            FILL_TYPE_CENTER -> {
                var radius = mViewSize / 2 * (mProgress.toFloat() / mMax)
                if (isStrokeShowing) {
                    radius = radius + 0.5f - mStrokePaint.strokeWidth
                }
                canvas.drawCircle(centerX, centerY, radius, mProgressPaint)
            }
            else -> throw IllegalArgumentException("Invalid Progress Fill = $progressFillType")
        }
        if (!TextUtils.isEmpty(mText) && isTextShowing) {
            if (!TextUtils.isEmpty(mTypeface)) {
                var typeface = sTypefaceCache[mTypeface]
                if (null == typeface && null != resources) {
                    val assets = resources.assets
                    if (null != assets) {
                        typeface = Typeface.createFromAsset(assets, mTypeface)
                        sTypefaceCache.put(mTypeface, typeface)
                    }
                }
                mTextPaint.typeface = typeface
            }
            val xPos = centerX.toInt()
            val yPos = (centerY - (mTextPaint.descent() + mTextPaint.ascent()) / 2).toInt()
            mText?.let { canvas.drawText(it, xPos.toFloat(), yPos.toFloat(), mTextPaint) }
        }
        if (null != mImage && isImageShowing) {
            val drawableSize = mImage!!.intrinsicWidth
            mImageRect[0, 0, drawableSize] = drawableSize
            mImageRect.offset((width - drawableSize) / 2, (height - drawableSize) / 2)
            mImage!!.bounds = mImageRect
            mImage!!.draw(canvas)
        }
        if (isStrokeShowing) {
            canvas.drawOval(mInnerRectF, mStrokePaint)
        }
    }

    /**
     * Gets the maximum progress value.
     */
    /**
     * Sets the maximum progress value. Defaults to 100.
     */
    var max: Int
        get() = mMax
        set(max) {
            require(!(max <= 0 || max < mProgress)) { String.format("Max (%d) must be > 0 and >= %d", max, mProgress) }
            mMax = max
            invalidate()
        }

    /**
     * Animates a progress fill of the view, using a Handler.
     */
    fun animateProgressFill() {
        mAnimationHandler.removeMessages(0)
        mAnimationHandler.setAnimateTo(mMax)
        mAnimationHandler.sendEmptyMessage(0)
        invalidate()
    }

    /**
     * Animates a progress fill of the view, using a Handler.
     *
     * @param animateTo - the progress value the animation should stop at (0 - MAX)
     */
    fun animateProgressFill(animateTo: Int) {
        mAnimationHandler.removeMessages(0)
        require(!(animateTo > mMax || animateTo < 0)) { String.format("Animation progress (%d) is greater than the max progress (%d) or lower than 0 ", animateTo, mMax) }
        mAnimationHandler.setAnimateTo(animateTo)
        mAnimationHandler.sendEmptyMessage(0)
        invalidate()
    }

    /**
     * Stops the views animation.
     */
    fun stopAnimating() {
        mAnimationHandler.removeMessages(0)
        mAnimationHandler.setAnimateTo(mProgress)
        invalidate()
    }

    /**
     * Gets the current progress from 0 to max.
     */
    /**
     * Sets the current progress (must be between 0 and max).
     */
    var progress: Int
        get() = mProgress
        set(progress) {
            require(!(progress > mMax || progress < 0)) { String.format("Progress (%d) must be between %d and %d", progress, 0, mMax) }
            mProgress = progress
            if (null != mListener) {
                if (mProgress == mMax) {
                    mListener!!.onProgressCompleted()
                } else {
                    mListener!!.onProgressChanged(mProgress, mMax)
                }
            }
            invalidate()
        }

    /**
     * Gets the color used to display the progress of the view.
     */
    /**
     * Sets the color used to display the progress of the view.
     *
     * @param color - color of the progress part of the view
     */
    var progressColor: Int
        get() = mProgressPaint.color
        set(color) {
            mProgressPaint.color = color
            invalidate()
        }

    /**
     * Gets the color used to display the background of the view.
     */
    fun getBackgroundColor(): Int {
        return mBackgroundPaint.color
    }

    /**
     * Sets the color used to display the background of the view.
     *
     * @param color - color of the background part of the view
     */
    override fun setBackgroundColor(color: Int) {
        mBackgroundPaint.color = color
        invalidate()
    }

    /**
     * Gets the color used to display the text of the view.
     */
    /**
     * Sets the color used to display the text of the view.
     *
     * @param color - color of the text part of the view
     */
    var textColor: Int
        get() = mTextPaint.color
        set(color) {
            mTextPaint.color = color
            invalidate()
        }

    /**
     * Sets the text size.
     *
     * @param sizeSp in sp for the text
     */
    fun setTextSize(sizeSp: Int) {
        textSize = sizeSp * mDisplayMetrics.scaledDensity
        mTextPaint.textSize = textSize
        invalidate()
    }

    /**
     * Gets the text of the view.
     */
    /**
     * Sets the text of the view.
     *
     * @param text to be displayed in the view
     */
    fun setText(text: String) {
        mText = text
        invalidate()
    }

    /**
     * Gets the typeface of the text.
     */
    /**
     * Sets the text typeface.
     * - i.e. fonts/Roboto/Roboto-Regular.ttf
     *
     * @param typeface that the text is displayed in
     */
    var typeface: String?
        get() = mTypeface
        set(typeface) {
            mTypeface = typeface
            invalidate()
        }

    /**
     * Sets the show text state.
     *
     * @param showText show or hide text
     */
    fun setShowText(showText: Boolean) {
        isTextShowing = showText
        invalidate()
    }

    /**
     * Get the color used to display the stroke of the view.
     */
    /**
     * Sets the color used to display the stroke of the view.
     *
     * @param color - color of the stroke part of the view
     */
    var strokeColor: Int
        get() = mStrokePaint.color
        set(color) {
            mStrokePaint.color = color
            invalidate()
        }

    /**
     * Set the stroke width.
     *
     * @param widthDp in dp for the pie border
     */
    fun setStrokeWidth(widthDp: Int) {
        strokeWidth = widthDp * mDisplayMetrics.density
        mStrokePaint.strokeWidth = strokeWidth
        invalidate()
    }

    /**
     * Sets the show stroke state.
     *
     * @param showStroke show or hide stroke
     */
    fun setShowStroke(showStroke: Boolean) {
        isStrokeShowing = showStroke
        invalidate()
    }

    /**
     * Gets the drawable of the view.
     */
    /**
     * Sets the drawable of the view.
     *
     * @param image drawable of the view
     */
    var imageDrawable: Drawable?
        get() = mImage
        set(image) {
            mImage = image
            invalidate()
        }

    /**
     * Sets the drawable of the view.
     *
     * @param resId resource id of the view's drawable
     */
    fun setImageResource(resId: Int) {
        if (null != resources) {
            mImage = ContextCompat.getDrawable(context, resId)
            invalidate()
        }
    }

    /**
     * Sets the show image state.
     *
     * @param showImage show or hide image
     */
    fun setShowImage(showImage: Boolean) {
        isImageShowing = showImage
        invalidate()
    }

    /**
     * Sets the progress listner.
     *
     * @param listener progress listener
     * @see OnProgressListener
     */
    fun setOnProgressListener(listener: OnProgressListener?) {
        mListener = listener
    }

    /**
     * Handler used to perform the fill animation.
     */
    private inner class AnimationHandler : Handler() {
        private var mAnimateTo = 0
        fun setAnimateTo(animateTo: Int) {
            mAnimateTo = animateTo
        }

        override fun handleMessage(msg: Message) {
            if (mProgress > mAnimateTo) {
                progress = mProgress - 1
                sendEmptyMessageDelayed(0, animationSpeed.toLong())
            } else if (mProgress < mAnimateTo) {
                progress = mProgress + 1
                sendEmptyMessageDelayed(0, animationSpeed.toLong())
            } else {
                removeMessages(0)
            }
        }
    }

    companion object {
        /**
         * Fills the progress radially in a clockwise direction.
         */
        const val FILL_TYPE_RADIAL = 0
        /**
         * Fills the progress expanding from the center of the view.
         */
        const val FILL_TYPE_CENTER = 1
        const val SLOW_ANIMATION_SPEED = 50
        const val MEDIUM_ANIMATION_SPEED = 25
        const val FAST_ANIMATION_SPEED = 1
        private const val DEFAULT_MAX = 100
        private const val DEFAULT_PROGRESS = 0
        private const val DEFAULT_START_ANGLE = -90
        private const val DEFAULT_STROKE_WIDTH = 3f
        private const val DEFAULT_TEXT_SIZE = 14f
        private const val DEFAULT_VIEW_SIZE = 96
        private val sTypefaceCache = LruCache<String?, Typeface?>(8)
    }

    init {
        init(context, attrs)
    }
}