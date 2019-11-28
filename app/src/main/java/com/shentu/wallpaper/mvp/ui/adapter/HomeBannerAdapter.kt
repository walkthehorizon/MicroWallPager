package com.shentu.wallpaper.mvp.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jess.arms.integration.AppManager
import com.shentu.wallpaper.model.entity.Banner
import com.shentu.wallpaper.mvp.ui.activity.BannerListActivity
import com.shentu.wallpaper.mvp.ui.activity.SubjectDetailActivity

class HomeBannerAdapter(private val banners: List<Banner>, private val context: Context) : PagerAdapter() {


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return banners.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val ivBanner = ImageView(container.context)
        container.addView(ivBanner, -1, -1)
        ivBanner.setOnClickListener {
            if (position == count - 1) {
                context.startActivity(Intent(context, BannerListActivity::class.java))
                return@setOnClickListener
            }
            SubjectDetailActivity.open(banners[position], context)
        }
        Glide.with(container.context)
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
