package com.shentu.paper.mvp.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.paper.R
import com.shentu.paper.app.GlideArms

import com.shentu.paper.model.entity.Banner

class BannerListAdapter(data: List<Banner>) : BaseQuickAdapter<Banner, BaseViewHolder>(
        R.layout.item_subject_list, data) {
    override fun convert(helper: BaseViewHolder?, item: Banner?) {
        helper?.getView<ImageView>(R.id.ivSubject)?.let {
            GlideArms.with(mContext)
                    .load(item?.imageUrl)
                    .transform(RoundedCorners(ConvertUtils.dp2px(12f)))
                    .into(it)
        }
    }
}