package com.shentu.wallpaper.mvp.ui.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
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
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlin.random.Random

class CategoryAdapter(data: MutableList<Category>) : BaseQuickAdapter<Category, BaseViewHolder>(R.layout.app_item_wallpaper_category, data) {

    init {
        setOnItemClickListener { adapter, _, position ->
            ARouter.getInstance()
                    .build("/activity/category/detail")
                    .withInt(CategoryDetailFragment.CATEGORY_ID, (adapter.data[position] as Category).id)
                    .navigation()
        }
    }

    override fun convert(helper: BaseViewHolder, item: Category) {
        GlideArms.with(helper.itemView.context)
                .load(if (TextUtils.isEmpty(item.logo))
                    ColorDrawable(Color.argb(255, Random.nextInt(256)
                            , Random.nextInt(256), Random.nextInt(256))) else item.logo)
                .transform(MultiTransformation<Bitmap>(BlurTransformation(25, 3),
                        CenterCrop(), RoundedCornersTransformation(ConvertUtils.dp2px(5.0f),
                        0, RoundedCornersTransformation.CornerType.ALL)))
                .into(helper.getView(R.id.ivCover))
        helper.setText(R.id.tv_name, item.name)
    }

}