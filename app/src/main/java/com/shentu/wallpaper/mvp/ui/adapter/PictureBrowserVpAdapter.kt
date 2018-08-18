package com.shentu.wallpaper.mvp.ui.adapter

import android.support.v4.view.PagerAdapter
import android.support.v4.view.PagerAdapter.POSITION_NONE
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.github.chrisbanes.photoview.PhotoView
import com.jess.arms.http.imageloader.glide.GlideArms
import com.shentu.wallpaper.app.EventBusTags
import com.shentu.wallpaper.app.event.SwitchNavigationEvent
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.app.utils.PicUtils
import com.shentu.wallpaper.model.entity.Wallpaper
import org.simple.eventbus.EventBus
import java.io.File

class PictureBrowserVpAdapter constructor(pictures: MutableList<Wallpaper>) : PagerAdapter() {

    private var pictures: MutableList<Wallpaper> = pictures

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var picturePath: String? = pictures[position].small_url
        val iv = PhotoView(container.context)
        iv.setOnPhotoTapListener(OnPhotoTapListener { view, x, y ->
            EventBus.getDefault().post(SwitchNavigationEvent(),EventBusTags.SWITCH_BROWSE_NAVIGATION)
        })
        val lp = ViewGroup.LayoutParams(-1, -2)
        val file = File(HkUtils.getInstance().getStrogePath(HkUtils.TYPE_BIG_PICTURE), pictures[position].big_url)
        if (FileUtils.isFileExists(file)) {
            picturePath = file.absolutePath
        }
        container.addView(iv, lp)
        GlideArms.with(container.context)
                .load(PicUtils.getInstance().getMM131GlideUrl(picturePath))
                .thumbnail(0.1f)
                .into(iv)
        return iv
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return pictures.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

}