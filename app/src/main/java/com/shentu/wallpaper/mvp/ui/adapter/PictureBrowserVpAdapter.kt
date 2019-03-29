package com.shentu.wallpaper.mvp.ui.adapter


import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.ui.fragment.PictureFragment

class PictureBrowserVpAdapter(fm: FragmentManager, private val wallpapers: MutableList<Wallpaper>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(p0: Int): androidx.fragment.app.Fragment {
        return PictureFragment.newInstance(wallpapers[p0])
    }

    override fun getCount(): Int {
        return wallpapers.size
    }

//    private var pictures: MutableList<Wallpaper>
//
//    constructor(pictures: MutableList<Wallpaper>) {
//        this.pictures = pictures
////        ProgressManager.getInstance().addResponseListener("https://raw.githubusercontent.com/JessYanCoding/MVPArmsTemplate/master/art/step.png", getGlideListener())
////        for (i in pictures) {
////            ProgressManager.getInstance().addResponseListener(i.url, getGlideListener())
////        }
//    }
//
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        //判断原图是否存在,若存在直接加载原图
//        var picturePath: String? = pictures[position].url
////        var picturePath: String? = "https://raw.githubusercontent.com/JessYanCoding/MVPArmsTemplate/master/art/step.png"
////        val file = File(PathUtils.getExternalPicturesPath(), URLUtil.guessFileName(pictures[position].origin_url
////                , null, null))
////        if (FileUtils.isFileExists(file)) {
////            picturePath = file.absolutePath
////        }
//
//        val parentView = RelativeLayout(container.context)
//
//        //添加ImageView
//        var lp = RelativeLayout.LayoutParams(-1, -2)
//        lp.addRule(RelativeLayout.CENTER_IN_PARENT)
//        val iv = PhotoView(container.context)
//        parentView.addView(iv, lp)
//
//        //添加LoadingView
//        lp = RelativeLayout.LayoutParams(ConvertUtils.dp2px(60F), ConvertUtils.dp2px(60F))
//        lp.addRule(RelativeLayout.CENTER_IN_PARENT)
//        val loadingView = PictureLoadingView(container.context)
//        parentView.addView(loadingView, lp)
//        loadingView.setTargetView(iv)
//
//        //添加父View到根布局
//        container.addView(parentView, -1, -1)
//
//        ProgressManager.getInstance().addResponseListener(picturePath, getGlideListener(loadingView))
//        GlideArms.with(container.context)
//                .load(picturePath)
//                .fitCenter()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(iv)
////        iv.setOnPhotoTapListener { _, _, _ ->
////            EventBusManager.getInstance().post(SwitchNavigationEvent())
////        }
//        return parentView
//    }
//
//    override fun isViewFromObject(p0: View, p1: Any): Boolean {
//        return p0 == p1
//    }
//
//    override fun getCount(): Int {
//        return pictures.size
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        container.removeView(`object` as View?)
//    }
//
//    override fun getItemPosition(`object`: Any): Int {
//        return POSITION_NONE
//    }
//
//    private fun getGlideListener(loadingView: PictureLoadingView): ProgressListener {
//        return object : ProgressListener {
//            override fun onProgress(progressInfo: ProgressInfo?) {
//                if (progressInfo?.isFinish!!) {
//                    loadingView.loadCompleted()
//                    return
//                }
//                Timber.e("id：" + progressInfo.id + "下载进度：" + (progressInfo.percent.toDouble() / 100))
//                loadingView.setProgress(progressInfo.percent.toDouble() / 100)
//            }
//
//            @SuppressLint("CheckResult")
//            override fun onError(id: Long, e: Exception?) {
//                Completable.fromAction {}
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({
//                            loadingView.loadFaild()
//                        }, {
//                            it.printStackTrace()
//                        })
//            }
//        }
//    }
//
//    private fun getGlideListener(): ProgressListener {
//        return object : ProgressListener {
//            override fun onProgress(progressInfo: ProgressInfo?) {
//                if (progressInfo?.isFinish!!) {
//                    Timber.e("picture success")
//                    return
//                }
//                Timber.e("picture progress:" + progressInfo.percent)
//            }
//
//            @SuppressLint("CheckResult")
//            override fun onError(id: Long, e: Exception?) {
//                Timber.e("picture failed")
//            }
//        }
//    }
}