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
import com.shentu.wallpaper.R
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
        return inflater.inflate(R.layout.fragment_picture, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        wallpaper = arguments!!["wallpaper"] as Wallpaper
        photoView.setImageViewFactory(GlideImageViewFactory())
//        photoView.setOnPhotoTapListener { _, _, _ ->
//            EventBusManager.getInstance().post(SwitchNavigationEvent())
//        }
//        photoView.setOnOutsidePhotoTapListener {
//            EventBusManager.getInstance().post(SwitchNavigationEvent())
//        }
//        photoView.minimumScale = 1f
//        photoView.maximumScale = 6.0f
//        photoView.mediumScale = 3f

        photoView.setOnClickListener {
            EventBusManager.getInstance().post(SwitchNavigationEvent())
        }

        //若原图存在直接加载原图
//        val picturePath: String? = if (wallpaper.isOriginExist) wallpaper.origin_url else wallpaper.url
        if (wallpaper.isOriginExist) {
            context?.let {
                photoView.showImage(Uri.parse(wallpaper.origin_url))
//                GlideArms.with(it)
//                        .load(wallpaper.origin_url)
//                        .transition(withCrossFade())
//                        .onlyRetrieveFromCache(true)
//                        .diskCacheStrategy(DiskCacheStrategy.DATA)
//                        .fitCenter()
//                        .fallback(R.drawable.default_cover_vertical)
//                        .into(photoView)
            }
        } else {
            context?.let {
                photoView.showImage(Uri.parse(wallpaper.url))
//                GlideArms.with(it)
//                        .load(wallpaper.url)
//                        .transition(withCrossFade())
//                        .fitCenter()
//                        .fallback(R.drawable.default_cover_vertical)
//                        .into(photoView)
            }
        }
    }

    override fun setData(data: Any?) {

    }

    @SuppressLint("MissingPermission")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadOriginPicture(event: LoadOriginPictureEvent) {
        if (event.id != wallpaper.id) {
            return
        }
        //ProgressManager.getInstance().addResponseListener(wallpaper.origin_url, getGlideListener(loadingView, wallpaper))
        context?.let {
            //            GlideArms.with(it)
//                    .load(wallpaper.origin_url)
//                    .fitCenter()
//                    .transition(withCrossFade())
//                    .diskCacheStrategy(DiskCacheStrategy.DATA)//仅缓存原数据
//                    .into(photoView)
            photoView.setProgressIndicator(ProgressPieIndicator())
            photoView.setImageLoaderCallback(getImageLoadCallback())
            photoView.showImage(Uri.parse(wallpaper.origin_url))
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

//    private fun getGlideListener(loadingView: PictureLoadingView, wallpaper: Wallpaper): ProgressListener {
//        return object : ProgressListener {
//            override fun onProgress(progressInfo: ProgressInfo?) {
//                if (progressInfo?.isFinish!!) {
//                    loadingView.loadCompleted()
//                    wallpaper.isOriginExist = true
//                    EventBus.getDefault().post(LoadOriginResultEvent(wallpaper.id, true))
//                    return
//                }
////                Timber.e("id：" + progressInfo.id + "下载进度：" + (progressInfo.percent.toDouble() / 100))
//                loadingView.setProgress(progressInfo.percent.toDouble() / 100)
//            }
//
//            @SuppressLint("CheckResult")
//            override fun onError(id: Long, e: Exception?) {
//                Completable.fromAction {}
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({
//                            EventBus.getDefault().post(LoadOriginResultEvent(wallpaper.id, false))
//                            loadingView.loadFaild()
//                        }, {
//                            it.printStackTrace()
//                            EventBus.getDefault().post(LoadOriginResultEvent(wallpaper.id, false))
//                        })
//            }
//        }
//    }
}