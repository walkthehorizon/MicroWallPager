package com.shentu.paper.mvp.ui.search

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import com.google.android.material.chip.ChipGroup
import com.google.gson.reflect.TypeToken
import com.shentu.paper.R
import com.shentu.paper.app.page.SearchHistoryCallback
import com.shentu.paper.app.utils.LimitQueue
import com.shentu.paper.databinding.LayoutSearchHistoryBinding
import kotlinx.android.synthetic.main.activity_search.*
import timber.log.Timber

class SearchHistoryView : FrameLayout {
    private var binding: LayoutSearchHistoryBinding =
        LayoutSearchHistoryBinding.inflate(LayoutInflater.from(context), this, true)
    private var queue: LimitQueue<String>
    private var callback: Callback? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val history = SPUtils.getInstance().getString("search_history", "")
        queue = if (history.isEmpty()) {
            LimitQueue(12)
        } else {
            GsonUtils.fromJson(history, object : TypeToken<LimitQueue<String>>() {}.type)
        }
        refresh()
    }

    private fun refresh() {
        binding.apply {
            chipGroup.removeAllViews()
            val lp = ChipGroup.LayoutParams(-2, ConvertUtils.dp2px(20f))
            chipGroup.chipSpacingVertical = ConvertUtils.dp2px(6f)
            for (key in queue.queue) {
                val chip = TextView(context)
                chip.minWidth = ConvertUtils.dp2px(40f)
                chip.setTextColor(ContextCompat.getColor(context, R.color.colorNormalText))
                val padding = ConvertUtils.dp2px(8.0f)
                chip.setPadding(padding, 0, padding, 0)
                chip.setBackgroundResource(R.drawable.bg_search_history_item)
                chip.text = key
                chip.gravity = Gravity.CENTER
                chip.setOnClickListener {
                    val keyStr = chip.text.toString()
                    callback?.selectKey(keyStr)
                }
                chipGroup.addView(chip, lp)
            }
            if (chipGroup.childCount > 0) {
                tvClear.visibility = View.VISIBLE
                tvClear.setOnClickListener {
                    chipGroup.removeAllViews()
                    queue.queue.clear()
                    SPUtils.getInstance().remove(KEY_SEARCH_HISTORY)
                    tvClear.visibility = View.GONE
                }
            }
        }

    }

    fun updateKey(key: String) {
        queue.offer(key, true)
        SPUtils.getInstance().put(SearchHistoryView.KEY_SEARCH_HISTORY, key)
        hide()
    }

    fun show() {
        refresh()
        if (visibility != VISIBLE) {
            visibility = View.VISIBLE
        }
    }

    private fun hide() {
        if (visibility == View.VISIBLE) {
            visibility = View.GONE
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    interface Callback {
        fun selectKey(key: String)
    }

    companion object {
        const val KEY_SEARCH_HISTORY = "key_search_history"
    }
}