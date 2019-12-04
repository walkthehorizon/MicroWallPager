package com.shentu.wallpaper.mvp.ui.adapter

import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.OnItemChildClickListener
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.model.entity.Subject

class HotAdapter(data: List<Subject?>) : BaseMultiItemQuickAdapter<Subject, BaseViewHolder>(data) {
    override fun convert(helper: BaseViewHolder, item: Subject) {
        helper.getView<View>(R.id.tv_content).visibility = View.GONE
        Glide.with(helper.itemView.context)
                .load(item.cover)
                .placeholder(R.drawable.default_head)
                .circleCrop()
                .into((helper.getView<View>(R.id.iv_avatar) as ImageView))
        helper.setText(R.id.tv_user_name, item.name)
        helper.setText(R.id.tv_content, item.name + "：" + item.description)
        helper.getView<View>(R.id.tv_support).isSelected = false
        if (item.itemType == Subject.ITEM_VIEW_1) {
            Glide.with(helper.itemView.context)
                    .load(item.cover)
                    .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(5f)))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into((helper.getView<View>(R.id.iv_cover) as ImageView))
        }
        if (item.itemType == Subject.ITEM_VIEW_2) {
            Glide.with(helper.itemView.context)
                    .load(HkUtils.instance.get2x2Image(item.cover))
                    .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(5f)))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into((helper.getView<View>(R.id.iv_1) as ImageView))
            Glide.with(helper.itemView.context)
                    .load(HkUtils.instance.get2x2Image(item.cover_1))
                    .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(5f)))
                    .into((helper.getView<View>(R.id.iv_2) as ImageView))
            Glide.with(helper.itemView.context)
                    .load(HkUtils.instance.get2x2Image(item.cover_2))
                    .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(5f)))
                    .into((helper.getView<View>(R.id.iv_3) as ImageView))
        }
        if (item.itemType == Subject.ITEM_VIEW_3) {
            Glide.with(helper.itemView.context)
                    .load(HkUtils.instance.get2x2Image(item.cover))
                    .transform(RoundedCorners(ConvertUtils.dp2px(5f)),CenterCrop())
                    .into((helper.getView<View>(R.id.iv_cover) as ImageView))
        }
        helper.addOnClickListener(R.id.tv_share)
        helper.addOnClickListener(R.id.tv_support)
        helper.addOnClickListener(R.id.iv_1)
        helper.addOnClickListener(R.id.iv_2)
        helper.addOnClickListener(R.id.iv_3)
    }

    init {
        addItemType(Subject.ITEM_VIEW_1, R.layout.app_item_hot_page_1)
        addItemType(Subject.ITEM_VIEW_2, R.layout.app_item_hot_page_2)
        addItemType(Subject.ITEM_VIEW_3, R.layout.app_item_hot_page_3)
        this.onItemChildClickListener = OnItemChildClickListener { _: BaseQuickAdapter<*, *>?, view: View, _: Int ->
            if (view.id == R.id.tv_comment) {
                ToastUtils.showShort("评论")
            }
            if (view.id == R.id.tv_support) {
                ToastUtils.showShort("点赞")
            }
        }
    }
}