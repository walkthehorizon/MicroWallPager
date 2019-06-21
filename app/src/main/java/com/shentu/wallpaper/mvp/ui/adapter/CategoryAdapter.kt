package com.shentu.wallpaper.mvp.ui.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.model.entity.Category
import com.shentu.wallpaper.mvp.ui.fragment.CategoryListActivity
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlin.random.Random

class CategoryAdapter(data: MutableList<Category>) : BaseQuickAdapter<Category, BaseViewHolder>(R.layout.app_item_wallpaper_category, data) {

    init {
        setOnItemClickListener { adapter, _, position ->
            val category = adapter.data[position] as Category
            CategoryListActivity.open(category.id, category.name)
        }
    }

    override fun convert(helper: BaseViewHolder, item: Category) {
        GlideArms.with(helper.itemView.context)
                .load(
                        if (TextUtils.isEmpty(item.logo))
                            ColorDrawable(Color.argb(112, Random.nextInt(255)
                                    , Random.nextInt(255), Random.nextInt(255)))
                        else
                            item.logo)
                .transform(MultiTransformation<Bitmap>(
//                        ColorFilterTransformation(Color.parseColor("#66FF0000")),
                        CenterCrop(), RoundedCornersTransformation(ConvertUtils.dp2px(5.0f),
                        0, RoundedCornersTransformation.CornerType.ALL)))
                .into(helper.getView(R.id.ivCover))
        helper.setText(R.id.tv_name, item.name)
    }

}