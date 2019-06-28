package com.shentu.wallpaper.mvp.ui.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.view.GlideImageViewFactory
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.shentu.wallpaper.model.entity.Wallpaper
import kotlinx.android.synthetic.main.fragment_picture.*
import java.io.File


class PictureFragment : BaseFragment<IPresenter>() {

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

    fun loadOriginPicture() {
        context?.let {
            photoView.setProgressIndicator(ProgressPieIndicator())
            photoView.setImageLoaderCallback(getImageLoadCallback())
            photoView.showImage(Uri.parse(wallpaper.originUrl))
        }
    }

    @SuppressLint("MissingPermission")
    fun saveImage() {
        photoView.saveImageIntoGallery()
    }

    private fun getImageLoadCallback(): ImageLoader.Callback {
        return object : ImageLoader.Callback {
            override fun onFinish() {

            }

            override fun onSuccess(image: File?) {
                callback?.onLoadOrigin(pos, true)
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

            }
        }
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