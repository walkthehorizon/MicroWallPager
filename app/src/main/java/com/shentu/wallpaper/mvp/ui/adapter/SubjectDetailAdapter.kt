package com.shentu.wallpaper.mvp.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.model.entity.Wallpaper

class SubjectDetailAdapter(data: List<Wallpaper>) : BaseQuickAdapter<Wallpaper, BaseViewHolder>(
        R.layout.item_subject_wallpaper, data) {
    override fun convert(helper: BaseViewHolder?, item: Wallpaper?) {
        helper?.getView<ImageView>(R.id.ivPaper)?.let {
            Glide.with(mContext)
                    .load(item?.url)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(5f))))
                    .into(it)
        }
    }

}