package com.shentu.wallpaper.mvp.ui.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.piasy.biv.loader.ImageLoader

import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.shentu.wallpaper.app.bigimage.GlideImageViewFactory
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.app.utils.PicUtils
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.ui.browser.Behavior
import com.shentu.wallpaper.mvp.ui.browser.SaveType
import com.shentu.wallpaper.mvp.ui.widget.progress.DefaultLoadCallback
import com.shentu.wallpaper.mvp.ui.widget.progress.ProgressPieIndicator
import kotlinx.android.synthetic.main.fragment_picture.*
import java.io.File


class PictureFragment : BaseFragment<IPresenter>() {

    private var normalLoad = false
    private var originLoad = false
    private var isOriginLoading = false

    companion object {
        fun newInstance(wallpaper: Wallpaper, pos: Int): PictureFragment {
            val args = Bundle()
            args.putSerializable("wallpaper", wallpaper)
            args.putInt("pos", pos)
            val fragment = PictureFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var wallpaper: Wallpaper
    var pos: Int = 0

    override fun setupFragmentComponent(appComponent: AppComponent) {

    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(com.shentu.wallpaper.R.layout.fragment_picture, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        pos = arguments?.get("pos") as Int
        wallpaper = arguments!!["wallpaper"] as Wallpaper
        photoView.setImageViewFactory(GlideImageViewFactory())
        photoView.setImageLoaderCallback(object : DefaultLoadCallback {
            override fun onSuccess(image: File?) {
                normalLoad = true
            }
        })
        photoView.setOnClickListener {
            callback?.switchNavigation()
        }

        //若原图存在直接加载原图
        context?.let {
            photoView.showImage(if (wallpaper.isOriginExist)
                Uri.parse(wallpaper.originUrl)
            else
                Uri.parse(wallpaper.url))
        }
    }

    override fun setData(data: Any?) {

    }

    fun loadOriginPicture(behavior: Behavior) {
        if (isOriginLoading) {
            return
        }
        context?.let {
            photoView.setProgressIndicator(ProgressPieIndicator())
            photoView.setImageLoaderCallback(getImageLoadCallback(behavior))
            photoView.showImage(Uri.parse(wallpaper.originUrl))
        }
    }

    private fun getImageLoadCallback(behavior: Behavior): ImageLoader.Callback {
        return object : ImageLoader.Callback {
            override fun onFinish() {
                isOriginLoading = false
            }

            @SuppressLint("MissingPermission")
            override fun onSuccess(image: File?) {
//                Timber.e(image?.absolutePath)
                originLoad = true
                callback?.onLoadOrigin(pos, true)
                if (behavior == Behavior.SET_WALLPAPER) {
                    image?.absolutePath?.let { HkUtils.setWallpaper(mContext, it) }
                }
                if (behavior == Behavior.ONLY_DOWNLOAD) {
                    copyPictureToLocal(SaveType.ORIGIN)
                }
            }

            override fun onFail(error: Exception?) {
                callback?.onLoadOrigin(pos, false)
            }

            override fun onCacheHit(imageType: Int, image: File?) {
            }

            override fun onCacheMiss(imageType: Int, image: File?) {
            }

            override fun onProgress(progress: Int) {

            }

            override fun onStart() {
                isOriginLoading = true
            }
        }
    }

    /**
     * @param type
     * @see SaveType
     * */
    fun savePicture(type: SaveType) {
        if (normalLoad && type == SaveType.NORMAL) {
            copyPictureToLocal(type)
            return
        }
        if (type == SaveType.ORIGIN) {
            if (originLoad) {
                copyPictureToLocal(type)
            } else {
                loadOriginPicture(Behavior.ONLY_DOWNLOAD)
            }
            return
        }
        ToastUtils.showShort("下载失败")
    }

    private fun copyPictureToLocal(type: SaveType) {
        val destPath = if (type == SaveType.NORMAL)
            PicUtils.getInstance().getDownloadPicturePath(wallpaper.url)
        else
            PicUtils.getInstance().getDownloadPicturePath(wallpaper.originUrl)
        FileUtils.copyFile(photoView.currentImageFile, File(destPath))
        ToastUtils.showShort("图片已保存在 手机相册》萌幻Cos")
    }

    interface Callback {
        fun switchNavigation()

        fun onLoadOrigin(pos: Int, result: Boolean)
    }

    private var callback: Callback? = null

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }
}