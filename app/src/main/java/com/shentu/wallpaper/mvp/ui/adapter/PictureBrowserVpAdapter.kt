package com.shentu.wallpaper.mvp.ui.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.RelativeLayout
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.github.chrisbanes.photoview.PhotoView
import com.jess.arms.http.imageloader.glide.GlideArms
import com.jess.arms.integration.EventBusManager
import com.shentu.wallpaper.app.event.SwitchNavigationEvent
import com.shentu.wallpaper.app.utils.PathUtils
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.ui.widget.PictureLoadingView
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo
import java.io.File

class PictureBrowserVpAdapter constructor(private var pictures: MutableList<Wallpaper>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //判断原图是否存在,若存在直接加载原图
        var picturePath: String? = pictures[position].url
        val file = File(PathUtils.getExternalPicturesPath(), URLUtil.guessFileName(pictures[position].origin_url
                , null, null))
        if (FileUtils.isFileExists(file)) {
            picturePath = file.absolutePath
        }

        val parentView = RelativeLayout(container.context)
        val iv = PhotoView(container.context)
        parentView.addView(iv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val loadingView = PictureLoadingView(container.context)
        parentView.addView(loadingView, ConvertUtils.dp2px(60F), ConvertUtils.dp2px(60F))
        loadingView.setTargetView(iv)

        ProgressManager.getInstance().addResponseListener(picturePath, getGlideListener(loadingView))
        GlideArms.with(container.context)
                .load(picturePath)
                .into(iv)

        container.addView(parentView)

        iv.setOnPhotoTapListener { _, _, _ ->
            EventBusManager.getInstance().post(SwitchNavigationEvent())
        }
        return parentView
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

    private fun getGlideListener(loadingView: PictureLoadingView): ProgressListener {
        return object : ProgressListener {
            override fun onProgress(progressInfo: ProgressInfo?) {
                if (progressInfo?.isFinish!!) {
                    loadingView.loadCompleted()
                    return
                }
                loadingView.setProgress(progressInfo.percent.toDouble())
            }

            override fun onError(id: Long, e: Exception?) {
                loadingView.loadFaild()
            }

        }
    }
}