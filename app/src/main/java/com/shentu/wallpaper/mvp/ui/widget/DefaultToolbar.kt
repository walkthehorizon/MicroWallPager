package com.shentu.wallpaper.mvp.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.shentu.wallpaper.R
import kotlinx.android.synthetic.main.default_toolbar.view.*
import java.util.*

class DefaultToolbar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : BaseRelativeLayout(context, attrs), View.OnClickListener {

    private val tlTitle: String?
    private val leftIcon: Int
    private val rightIcon: Int
    private val rightIcon2: Int
    private val rightText: String?
    private val endTextSize: Float
    private val startIconColor: Int
    override fun getLayoutId(): Int {
        return -1
    }

    override fun initView(context: Context, attrs: AttributeSet) {}

    fun getLeftIcon(): Int {
        return leftIcon
    }

    private fun setRightIcon2(icon: Int) {
        if (icon != -1) {
            ivEnd2.visibility = View.VISIBLE
            ivEnd2.setImageResource(icon)
            ivEnd2.setOnClickListener(this)
        }
    }

    private fun setLeftIcon(icon: Int) {
        if (icon != -1) {
            leftIconView.visibility = View.VISIBLE
            leftIconView.imageTintList = ColorStateList.valueOf(startIconColor)
            leftIconView.setImageResource(icon)
            leftIconView.setOnClickListener(this)
        }
    }

    private fun setRightIcon(icon: Int) {
        if (icon != -1) {
            rightIconView.visibility = View.VISIBLE
            rightIconView.setImageResource(icon)
            rightIconView.setOnClickListener(this)
        }
    }

    private fun setRightText(str: String?) {
        if (!TextUtils.isEmpty(str)) {
            tvRight!!.visibility = View.VISIBLE
            tvRight!!.text = str
            tvRight!!.textSize = endTextSize
            tvRight!!.setOnClickListener(this)
        }
    }

    fun setTitle(text: String?) {
        tvTitle.text = text ?: ""
    }

    override fun onClick(view: View) {
        if (listeners == null) {
            return
        }
        for (i in listeners!!.indices) {
            when (view.id) {
                R.id.rightIconView -> listeners!![i].onClickRightIcon()
                R.id.leftIconView -> listeners!![i].onClickLeftIcon()
                R.id.tvRight -> listeners!![i].clickRightText()
                R.id.ivEnd2 -> listeners!![i].onClickRightIcon2()
            }
        }
    }

    private var listeners: MutableList<OnClickListener>? = null

    open class OnClickListener {
        open fun onClickLeftIcon() {}
        open fun onClickRightIcon() {}
        open fun onClickRightIcon2() {}
        open fun clickRightText() {}
    }

    fun addOnClickListener(listener: OnClickListener?) {
        if (listeners == null) {
            listeners = ArrayList()
        }
        if (listener == null) {
            return
        }
        listeners!!.add(listener)
    }

    init {
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.addRule(RelativeLayout.CENTER_IN_PARENT)
        addView(LayoutInflater.from(context).inflate(R.layout.default_toolbar, this, false), lp)
        ButterKnife.bind(this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.DefaultToolbar)
        tlTitle = a.getString(R.styleable.DefaultToolbar_tl_title)
        leftIcon = a.getResourceId(R.styleable.DefaultToolbar_tl_left_icon, R.drawable.ic_arrow_back_white_24dp)
        rightIcon = a.getResourceId(R.styleable.DefaultToolbar_tl_right_icon, -1)
        rightText = a.getString(R.styleable.DefaultToolbar_tl_right_text)
        rightIcon2 = a.getResourceId(R.styleable.DefaultToolbar_tl_right_icon2, -1)
        endTextSize = a.getDimension(R.styleable.DefaultToolbar_tl_end_text_size, 16f)
        startIconColor = a.getColor(R.styleable.DefaultToolbar_tl_left_tint, Color.WHITE)
        a.recycle()
        setTitle(tlTitle)
        setLeftIcon(leftIcon)
        setRightIcon(rightIcon)
        setRightText(rightText)
        setRightIcon2(rightIcon2)
    }
}