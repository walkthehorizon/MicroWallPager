package com.shentu.wallpaper.mvp.ui.adapter

import android.graphics.Bitmap
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.model.entity.Category
import com.shentu.wallpaper.mvp.ui.fragment.CategoryDetailFragment
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class CategoryAdapter(data: MutableList<Category>?) : BaseQuickAdapter<Category, BaseViewHolder>(R.layout.app_item_wallpaper_category, data) {

    init {
        setOnItemClickListener { adapter, _, position ->
            ARouter.getInstance()
                    .build("activity/category/list")
                    .withInt(CategoryDetailFragment.CATEGORY_ID, (adapter.data[position] as Category).id)
                    .navigation()
        }
    }

    override fun convert(helper: BaseViewHolder, item: Category) {
        GlideArms.with(helper.itemView.context)
                .load(item.logo)
                .transform(MultiTransformation<Bitmap>(CenterCrop(), RoundedCornersTransformation(ConvertUtils.dp2px(5.0f), 0, RoundedCornersTransformation.CornerType.ALL)))
                .into(helper.getView(R.id.iv_cover))
        helper.setText(R.id.tv_name, item.name)
    }

}