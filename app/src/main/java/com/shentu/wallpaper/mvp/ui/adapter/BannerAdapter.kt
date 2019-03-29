package com.shentu.wallpaper.mvp.ui.adapter

import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.model.entity.Banner

class BannerAdapter(data: List<Banner>?) : BaseQuickAdapter<Banner, BaseViewHolder>(R.layout.item_home_banner, data) {

    init {
        setOnItemClickListener { adapter, view, position ->
            ToastUtils.showShort(position)
        }
    }

    override fun convert(helper: BaseViewHolder?, item: Banner?) {
        GlideArms.with(helper!!.itemView.context)
                .load(item?.imageUrl)
                .centerCrop()
                .into(helper.getView(R.id.ivBanner))
    }
}