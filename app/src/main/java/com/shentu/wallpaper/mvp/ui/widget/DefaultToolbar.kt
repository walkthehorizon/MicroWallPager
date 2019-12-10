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
import timber.log.Timber
import java.util.*

class DefaultToolbar @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = -1)
    : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    val isBack: Boolean

    init {
        addView(LayoutInflater.from(context).inflate(R.layout.default_toolbar,this,false))
        val a = context.obtainStyledAttributes(attrs, R.styleable.DefaultToolbar)
        val tlTitle = a.getString(R.styleable.DefaultToolbar_tl_title)
        val leftIcon = a.getResourceId(R.styleable.DefaultToolbar_tl_left_icon, -1)
        val rightIcon = a.getResourceId(R.styleable.DefaultToolbar_tl_right_icon, -1)
        val rightText = a.getString(R.styleable.DefaultToolbar_tl_right_text)
        val rightIcon2 = a.getResourceId(R.styleable.DefaultToolbar_tl_right_icon2, -1)
        val endTextSize = a.getDimension(R.styleable.DefaultToolbar_tl_end_text_size, 16f)
        val startIconColor = a.getColor(R.styleable.DefaultToolbar_tl_left_tint, Color.WHITE)
        a.recycle()
        Timber.e("left:  $leftIcon")
        isBack = leftIcon == R.drawable.ic_arrow_back_white_24dp
        setTitle(tlTitle)
        setLeftIcon(leftIcon, startIconColor)
        setRightIcon(rightIcon)
        setRightText(rightText, endTextSize)
        setRightIcon2(rightIcon2)
    }

    private fun setRightIcon2(icon: Int) {
        if (icon != -1) {
            ivEnd2.visibility = View.VISIBLE
            ivEnd2.setImageResource(icon)
            ivEnd2.setOnClickListener(this)
        }
    }

    private fun setLeftIcon(leftIcon: Int, startIconColor: Int) {
        if (leftIcon != -1) {
            leftIconView.visibility = View.VISIBLE
            leftIconView.imageTintList = ColorStateList.valueOf(startIconColor)
            leftIconView.setImageResource(leftIcon)
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

    private fun setRightText(str: String?, endTextSize: Float) {
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
}