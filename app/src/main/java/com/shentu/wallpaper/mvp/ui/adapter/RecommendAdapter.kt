package com.shentu.wallpaper.mvp.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.ConvertUtils
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
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.model.entity.Wallpaper
import java.util.*

class RecommendAdapter(val context: Context, private var wallpapers: List<Wallpaper>) : BaseQuickAdapter<Wallpaper, BaseViewHolder>(
        R.layout.item_rv_recommend, wallpapers) {

    override fun convert(helper: BaseViewHolder, item: Wallpaper) {
        val cardView: CardView = helper.getView(R.id.cardView)
        val ivPicture: ImageView = helper.getView(R.id.ivPicture)
        Glide.with(mContext)
                .load(HkUtils.instance.get2x2Image(item.url))
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

    /**
     * 重写以实现瀑布流布局时无缝嵌入head
     * */
    override fun setFullSpan(holder: RecyclerView.ViewHolder?) {
        if (holder!!.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            val params: StaggeredGridLayoutManager.LayoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            params.isFullSpan = false
        }
    }

}
