package com.shentu.paper.mvp.ui.adapter

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.paper.R
import com.shentu.paper.app.GlideArms
import com.shentu.paper.app.utils.HkUtils
import com.shentu.paper.model.entity.Wallpaper

class CategoryListAdapter(data: MutableList<Wallpaper> = mutableListOf()) : BaseQuickAdapter<Wallpaper
        , BaseViewHolder>(R.layout.item_rv_category_list, data) {
    override fun convert(helper: BaseViewHolder, item: Wallpaper) {
        GlideArms.with(mContext)
                .load(HkUtils.instance.get2x2Image(item.url))
                .placeholder(R.drawable.ic_twotone_broken_image_24)
                .into((helper.getView<View>(R.id.iv_small) as ImageView))
    }
}