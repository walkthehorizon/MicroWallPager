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
import com.jess.arms.integration.EventBusManager
import com.jess.arms.mvp.IPresenter
import com.shentu.wallpaper.app.event.LoadOriginPictureEvent
import com.shentu.wallpaper.app.event.LoadOriginResultEvent
import com.shentu.wallpaper.app.event.SwitchNavigationEvent
import com.shentu.wallpaper.model.entity.Wallpaper
import kotlinx.android.synthetic.main.fragment_picture.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


class PictureFragment : BaseFragment<IPresenter>() {

    companion object {
        fun newInstance(wallpaper: Wallpaper): PictureFragment {
            val args = Bundle()
            args.putSerializable("wallpaper", wallpaper)
            val fragment = PictureFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var wallpaper: Wallpaper

    override fun setupFragmentComponent(appComponent: AppComponent) {

    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(com.shentu.wallpaper.R.layout.fragment_picture, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        wallpaper = arguments!!["wallpaper"] as Wallpaper
        photoView.setImageViewFactory(GlideImageViewFactory())
        photoView.setOnClickListener {
            EventBusManager.getInstance().post(SwitchNavigationEvent())
        }

        //若原图存在直接加载原图
        context?.let {
            photoView.showImage(if (wallpaper.isOriginExist)
                Uri.parse(wallpaper.originUrl)
            else
                Uri.parse(wallpaper.url))
        }
//        val transitionSet = TransitionSet()
//        transitionSet.addTransition(ChangeBounds())
//        transitionSet.addTransition(ChangeTransform())
//        transitionSet.addTarget(photoView)
//        activity?.window?.sharedElementEnterTransition = transitionSet
//        activity?.window?.sharedElementExitTransition = transitionSet
    }

    override fun setData(data: Any?) {

    }

    @SuppressLint("MissingPermission")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadOriginPicture(event: LoadOriginPictureEvent) {
        if (event.id != wallpaper.id) {
            return
        }
        context?.let {
            photoView.setProgressIndicator(ProgressPieIndicator())
            photoView.setImageLoaderCallback(getImageLoadCallback())
            photoView.showImage(Uri.parse(wallpaper.originUrl))
        }
    }

    private fun getImageLoadCallback(): ImageLoader.Callback {
        return object : ImageLoader.Callback {
            override fun onFinish() {

            }

            override fun onSuccess(image: File?) {
                wallpaper.isOriginExist = true
                EventBus.getDefault().post(LoadOriginResultEvent(wallpaper.id, true))
            }

            override fun onFail(error: Exception?) {
                EventBus.getDefault().post(LoadOriginResultEvent(wallpaper.id, false))
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
}