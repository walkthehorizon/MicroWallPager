package com.shentu.wallpaper.mvp.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.palette.graphics.Palette
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.florent37.glidepalette.BitmapPalette.Profile.MUTED_LIGHT
import com.github.florent37.glidepalette.GlidePalette
import com.shentu.wallpaper.R
import com.shentu.wallpaper.model.entity.Wallpaper
import java.util.*

class RecommendAdapter(val context: Context, private var wallpapers: MutableList<Wallpaper>,
                       private var decoration: Float) : BaseQuickAdapter<Wallpaper,
        BaseViewHolder>(R.layout.item_rv_recommend, wallpapers) {

    override fun convert(helper: BaseViewHolder, item: Wallpaper) {
        val cardView: CardView = helper.getView(R.id.cardView)
        val ivPicture: ImageView = helper.getView(R.id.ivPicture)
        val lp: ViewGroup.LayoutParams = ivPicture.layoutParams
        if (helper.layoutPosition == 0) {
            lp.height = ((ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(decoration * 3)) /
                    2 * 383 / 900f).toInt()
            ivPicture.layoutParams = lp
        } else {
            lp.height = ConvertUtils.dp2px(300.0f)
            ivPicture.layoutParams = lp
        }
        Glide.with(mContext)
                .load(item.url)
                .listener(GlidePalette.with(item.url)
                        .use(MUTED_LIGHT)
                        .intoCallBack { palette ->
                            (cardView).setCardBackgroundColor(Objects.requireNonNull<Palette>(palette)
                                    .getLightVibrantColor(Color.WHITE))
                        })
                .transform(MultiTransformation<Bitmap>(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(5f))))
                .transition(withCrossFade())
                .into(ivPicture)
    }

    override fun setNewData(data: MutableList<Wallpaper>?) {
        this.wallpapers = data!!
        super.setNewData(data)
    }

}
