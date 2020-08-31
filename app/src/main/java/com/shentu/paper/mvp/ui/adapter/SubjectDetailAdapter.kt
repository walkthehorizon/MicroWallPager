package com.shentu.paper.mvp.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.paper.R
import com.shentu.paper.app.GlideArms
import com.shentu.paper.model.entity.Wallpaper

class SubjectDetailAdapter(data: List<Wallpaper>) : BaseQuickAdapter<Wallpaper, BaseViewHolder>(
        R.layout.item_subject_wallpaper, data) {
    override fun convert(helper: BaseViewHolder?, item: Wallpaper?) {
        helper?.getView<ImageView>(R.id.ivPaper)?.let {
            GlideArms.with(mContext)
                    .load(item?.url)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(5f))))
                    .into(it)
        }
    }

}