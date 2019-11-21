package com.shentu.wallpaper.mvp.ui.adapter

import android.view.View
import android.widget.CheckBox
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.model.entity.Wallpaper

class CollectListAdapter(data: List<Wallpaper>?, val decoration: Int) : BaseQuickAdapter<Wallpaper
        , BaseViewHolder>(R.layout.item_collect, data) {
    private var multiChecked = false

    override fun convert(helper: BaseViewHolder, item: Wallpaper?) {
        Glide.with(mContext)
                .load(item?.url)
                .placeholder(R.drawable.ic_pets_black_24dp)
                .centerCrop()
                .into(helper.getView(R.id.ivCover))
        helper.getView<CheckBox>(R.id.checkbox).visibility = if (multiChecked) View.VISIBLE else View.GONE
        item?.checked?.let { helper.setChecked(R.id.checkbox, it) }
    }

    fun notifyModeChanged(mode: Boolean) {
        multiChecked = mode
        this.notifyDataSetChanged()
    }

    fun getMode(): Boolean {
        return multiChecked
    }
}