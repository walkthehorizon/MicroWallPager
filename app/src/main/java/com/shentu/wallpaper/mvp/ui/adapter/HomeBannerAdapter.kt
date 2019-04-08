package com.shentu.wallpaper.mvp.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.model.entity.Banner

class HomeBannerAdapter(private val banners: List<Banner>) : PagerAdapter() {


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return banners.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val ivBanner = ImageView(container.context)
        container.addView(ivBanner, -1, -1)
        ivBanner.setOnClickListener { ToastUtils.showShort(position) }
        GlideArms.with(container.context)
                .load(banners[position].imageUrl)
                .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(8.0f)))
                .into(ivBanner)
        return ivBanner
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

//    fun getFirstPosition(): Int {
//        if (this.count == 0) {
//            return 0
//        }
//        return banners.size * factor / 2
//    }
}
