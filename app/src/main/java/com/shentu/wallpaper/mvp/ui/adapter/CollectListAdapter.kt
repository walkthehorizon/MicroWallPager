package com.shentu.wallpaper.mvp.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.model.entity.Wallpaper

class CollectListAdapter(data: List<Wallpaper>?, val decoration: Int) : BaseQuickAdapter<Wallpaper
        , BaseViewHolder>(R.layout.item_collect, data) {
    override fun convert(helper: BaseViewHolder, item: Wallpaper?) {
        GlideArms.with(mContext)
                .load(item?.url)
                .placeholder(R.drawable.ic_pets_black_24dp)
                .centerCrop()
                .into(helper.getView(R.id.ivCover))
    }
}