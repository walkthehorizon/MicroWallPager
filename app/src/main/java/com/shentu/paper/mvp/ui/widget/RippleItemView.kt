package com.shentu.paper.mvp.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.shentu.paper.R
import kotlinx.android.synthetic.main.layout_ripple_item.view.*

class RippleItemView : RelativeLayout {

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RippleItemView)
        val leftIconId = ta.getResourceId(R.styleable.RippleItemView_riv_icon, -1)
        val title = ta.getString(R.styleable.RippleItemView_riv_title)
        val isVisibleArrow = ta.getBoolean(R.styleable.RippleItemView_riv_arrow_visible, true)
        val isVisibleDivider = ta.getBoolean(R.styleable.RippleItemView_riv_divider_visible, true)
        val iconTint = ta.getColor(R.styleable.RippleItemView_riv_icon_tint,
                ContextCompat.getColor(context, R.color.colorAccent))
        val endValue = ta.getString(R.styleable.RippleItemView_riv_end_content)
        ta.recycle()
        LayoutInflater.from(context).inflate(R.layout.layout_ripple_item, this, true)
        if (leftIconId != -1) {
            leftIcon.imageTintList = ColorStateList.valueOf(iconTint)
            leftIcon.setImageResource(leftIconId)
        } else {
            leftIcon.visibility = View.GONE
        }
        endValue?.let {
            tvEndValue.text = it
        }
        tvTitle.text = title
        tvTitle.visibility = if (isVisibleArrow) View.VISIBLE else View.INVISIBLE
        tvTitle.visibility = if (isVisibleDivider) View.VISIBLE else View.INVISIBLE
    }

    fun setEndValue(value: String?) {
        tvEndValue.text = value
    }

    fun getEndValue(): String? {
        return tvEndValue.text.toString()
    }
}