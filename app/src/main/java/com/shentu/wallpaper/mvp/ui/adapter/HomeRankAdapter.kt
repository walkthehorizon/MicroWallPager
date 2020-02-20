package com.shentu.wallpaper.mvp.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.model.entity.Wallpaper

class HomeRankAdapter(data: List<Wallpaper>)
    : BaseQuickAdapter<Wallpaper, BaseViewHolder>(R.layout.item_home_newest, data) {

    override fun convert(helper: BaseViewHolder, item: Wallpaper) {
        val ivPicture = helper.getView<ImageView>(R.id.ivPicture)
        GlideArms.with(mContext)
                .load(HkUtils.instance.get2x2Image(item.url))
                .error(R.drawable.ic_error_black_24dp)
                .placeholder(R.drawable.default_category_placeholder)
                .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(12f)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivPicture)
    }
}