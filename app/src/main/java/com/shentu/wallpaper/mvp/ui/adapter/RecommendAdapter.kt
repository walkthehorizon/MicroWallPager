package com.shentu.wallpaper.mvp.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.palette.graphics.Palette
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener
import com.chad.library.adapter.base.BaseViewHolder
import com.github.florent37.glidepalette.BitmapPalette.Profile.MUTED_LIGHT
import com.github.florent37.glidepalette.GlidePalette
import com.jess.arms.integration.AppManager
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.ui.activity.BannerListActivity
import com.shentu.wallpaper.mvp.ui.activity.PictureBrowserActivity
import java.util.*
import kotlin.collections.ArrayList

class RecommendAdapter(val context: Context, data: MutableList<Wallpaper>) : BaseQuickAdapter<Wallpaper
        , BaseViewHolder>(R.layout.item_rv_recommend, data) {

    private var wallpapers: List<Wallpaper> = ArrayList()

    init {
        onItemClickListener = OnItemClickListener { _, view, position ->
            if (position == 0) {//主题列表
                AppManager.getAppManager().startActivity(Intent(context, BannerListActivity::class.java))
                return@OnItemClickListener
            }
            val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view
                    , view.width / 2, view.height / 2
                    , 0, 0)
            PictureBrowserActivity.open(context, wallpapers, position, compat)
        }
    }

    override fun convert(helper: BaseViewHolder, item: Wallpaper) {
        val cardView: CardView = helper.getView(R.id.cardView)
        val ivPicture: ImageView = helper.getView(R.id.ivPicture)
        val lp: ViewGroup.LayoutParams = ivPicture.layoutParams
        if (helper.layoutPosition == 0) {
            lp.height = ConvertUtils.dp2px(100.0f)
            ivPicture.layoutParams = lp
        } else {
            lp.height = ConvertUtils.dp2px(300.0f)
            ivPicture.layoutParams = lp
        }
        GlideArms.with(mContext)
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
