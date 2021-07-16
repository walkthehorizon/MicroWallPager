package com.shentu.paper.mvp.ui.adapter

import android.view.View
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.paper.R
import com.shentu.paper.app.GlideApp
import com.shentu.paper.app.utils.HkUtils
import com.shentu.paper.model.entity.Wallpaper

class CollectListAdapter(data: List<Wallpaper>?, val decoration: Int) : BaseQuickAdapter<Wallpaper
        , BaseViewHolder>(R.layout.item_collect, data) {
    private var multiChecked = false

    override fun convert(helper: BaseViewHolder, item: Wallpaper) {
        GlideApp.with(mContext)
                .load(HkUtils.instance.get2x2Image(item.url))
                .placeholder(R.drawable.ic_pets_black_24dp)
                .centerCrop()
                .into(helper.getView(R.id.ivCover))
        helper.getView<CheckBox>(R.id.checkbox).visibility = if (multiChecked) View.VISIBLE else View.GONE
        item.checked.let { helper.setChecked(R.id.checkbox, it) }
    }

    fun notifyModeChanged(mode: Boolean) {
        multiChecked = mode
        this.notifyDataSetChanged()
    }

    fun getMode(): Boolean {
        return multiChecked
    }
}