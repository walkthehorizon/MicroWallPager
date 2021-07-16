package com.shentu.paper.mvp.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.paper.R
import com.shentu.paper.app.GlideApp
import com.shentu.paper.app.utils.HkUtils
import com.shentu.paper.model.entity.WallpaerSection

class HomeNewestAdapter(data: List<WallpaerSection>)
    : BaseSectionQuickAdapter<WallpaerSection, BaseViewHolder>(R.layout.item_home_newest
        , R.layout.item_home_newest_section, data) {

    override fun convertHead(helper: BaseViewHolder, item: WallpaerSection) {
        helper.setText(R.id.tvTime, item.header)
    }

    override fun convert(helper: BaseViewHolder, item: WallpaerSection) {
        val ivPicture = helper.getView<ImageView>(R.id.ivPicture)
        GlideApp.with(mContext)
                .load(HkUtils.instance.get2x2Image(item.t!!.url))
                .error(R.drawable.ic_error_black_24dp)
                .placeholder(R.drawable.default_category_placeholder)
                .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(12f)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPicture)
    }

    fun getHeadType(): Int {
        return SECTION_HEADER_VIEW
    }

}