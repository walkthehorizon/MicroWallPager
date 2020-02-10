package com.shentu.wallpaper.mvp.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.model.entity.Comment

class CommentAdapter(data: MutableList<Comment>) : BaseQuickAdapter<Comment, BaseViewHolder>(
        R.layout.item_dialog_comment, data) {
    override fun convert(helper: BaseViewHolder, item: Comment) {
        GlideArms.with(helper.itemView)
                .load(item.avatar)
                .centerCrop()
                .circleCrop()
                .into(helper.getView(R.id.ivAvatar))
        helper.setText(R.id.tvName, item.nickname)
        helper.setText(R.id.tvComment, item.content)
        helper.setText(R.id.tvCreated, item.created)
    }
}