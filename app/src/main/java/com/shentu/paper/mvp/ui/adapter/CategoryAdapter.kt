package com.shentu.paper.mvp.ui.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.paper.R
import com.shentu.paper.app.GlideApp
import com.shentu.paper.app.utils.HkUtils
import com.shentu.paper.model.entity.Category
import com.shentu.paper.mvp.ui.fragment.CategoryListActivity
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class CategoryAdapter(data: MutableList<Category>) : BaseQuickAdapter<Category, BaseViewHolder>(R.layout.app_item_wallpaper_category, data) {

    private val defaultDrawable = ColorDrawable(Color.LTGRAY)

    init {
        setOnItemClickListener { adapter, _, position ->
            val category = adapter.data[position] as Category
            CategoryListActivity.open(category.id, category.name)
        }
    }

    override fun convert(helper: BaseViewHolder, item: Category) {
        GlideApp.with(mContext)
                .load(HkUtils.instance.get2x2Image(item.logo))
                .override(480, 270)
                .placeholder(R.drawable.default_category_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transform(MultiTransformation<Bitmap>(
                        CenterCrop(), RoundedCornersTransformation(ConvertUtils.dp2px(5f),
                        0, RoundedCornersTransformation.CornerType.ALL)))
                .transition(withCrossFade())
                .into(helper.getView(R.id.ivCover))
        helper.setText(R.id.tv_name, item.name)
    }

}