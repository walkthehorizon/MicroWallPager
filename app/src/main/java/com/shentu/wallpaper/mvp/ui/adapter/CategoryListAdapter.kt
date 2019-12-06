package com.shentu.wallpaper.mvp.ui.adapter

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.model.entity.Wallpaper

class CategoryListAdapter(data: List<Wallpaper>) : BaseQuickAdapter<Wallpaper
        , BaseViewHolder>(R.layout.item_rv_category_list, data) {
    override fun convert(helper: BaseViewHolder, item: Wallpaper) {
        GlideArms.with(mContext)
                .load(HkUtils.instance.get2x2Image(item.url))
                .placeholder(R.drawable.default_cover_vertical)
                .into((helper.getView<View>(R.id.iv_small) as ImageView))
    }
}