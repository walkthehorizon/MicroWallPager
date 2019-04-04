package com.shentu.wallpaper.mvp.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.palette.graphics.Palette
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.florent37.glidepalette.BitmapPalette.Profile.VIBRANT_LIGHT
import com.github.florent37.glidepalette.GlidePalette
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.model.entity.RecommendWallpaperList
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.ui.activity.PictureBrowserActivity
import java.util.*

class RecommendAdapter(context: Context, data: List<Wallpaper>?) : BaseQuickAdapter<Wallpaper
        , BaseViewHolder>(R.layout.item_rv_recommend, data) {

    private val wallpaperList: RecommendWallpaperList = RecommendWallpaperList()

    init {
        setOnItemClickListener { _, _, _ -> PictureBrowserActivity.open(context,  wallpaperList) }
    }

    override fun convert(helper: BaseViewHolder, item: Wallpaper) {
        GlideArms.with(helper.itemView.context)
                .load(item.url)
                .listener(GlidePalette.with(item.url)
                        .use(VIBRANT_LIGHT)
                        .intoCallBack { palette -> (helper.getView<View>(R.id.cardView) as CardView).setCardBackgroundColor(Objects.requireNonNull<Palette>(palette).getLightMutedColor(Color.LTGRAY)) })
                .transform(MultiTransformation<Bitmap>(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(5f))))
                .transition(withCrossFade())
                .into(helper.getView<View>(R.id.ivPicture) as ImageView)
    }

    override fun setNewData(data: MutableList<Wallpaper>?) {
        wallpaperList.wallpapers = data
        super.setNewData(data)
    }
}
