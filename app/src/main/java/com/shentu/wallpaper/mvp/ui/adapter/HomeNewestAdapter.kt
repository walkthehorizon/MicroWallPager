package com.shentu.wallpaper.mvp.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.model.entity.WallpaerSection

class HomeNewestAdapter(data: List<WallpaerSection>)
    : BaseSectionQuickAdapter<WallpaerSection, BaseViewHolder>(R.layout.item_home_newest
        ,R.layout.item_home_newest_section, data) {

    override fun convertHead(helper: BaseViewHolder, item: WallpaerSection) {
        helper.setText(R.id.tvTime,item.header)
    }

    override fun convert(helper: BaseViewHolder, item: WallpaerSection) {
        val ivPicture = helper.getView<ImageView>(R.id.ivPicture)
        Glide.with(mContext)
                .load(item.t.url)
                .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(12f)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPicture)
    }

    fun getHeadType(): Int {
        return SECTION_HEADER_VIEW
    }

}