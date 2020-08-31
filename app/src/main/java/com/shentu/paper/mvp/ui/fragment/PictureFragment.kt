package com.shentu.paper.mvp.ui.fragment

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.piasy.biv.loader.ImageLoader
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.shentu.paper.app.bigimage.GlideImageViewFactory
import com.shentu.paper.app.utils.HkUtils
import com.shentu.paper.app.utils.PicUtils
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.mvp.ui.browser.Behavior
import com.shentu.paper.mvp.ui.browser.SaveType
import com.shentu.paper.mvp.ui.widget.progress.ProgressPieIndicator
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.fragment_picture.*
import timber.log.Timber
import java.io.File

class PictureFragment : BaseFragment<IPresenter>() {

    //    private var normalLoad = false
//    private var originLoad = false
    private var isLoading = false
    private var normalFile: File? = null
    private var originFile: File? = null

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
        return inflater.inflate(com.shentu.paper.R.layout.fragment_picture, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        pos = arguments?.get("pos") as Int
        wallpaper = requireArguments()["wallpaper"] as Wallpaper
        photoView.setImageViewFactory(GlideImageViewFactory())
        photoView.setOnClickListener {
            callback?.switchNavigation()
        }
        loadPicture(Behavior.LOAD_NORMAL)
    }

    override fun setData(data: Any?) {

    }

    fun loadPicture(behavior: Behavior) {
        if (isLoading) {
            return
        }
        context?.let {
            photoView.setProgressIndicator(ProgressPieIndicator())
            photoView.setImageLoaderCallback(getImageLoadCallback(behavior))
            photoView.showImage(Uri.parse(if (behavior == Behavior.ONLY_DOWNLOAD_ORIGIN
                    || behavior == Behavior.LOAD_ORIGIN)
                wallpaper.originUrl else wallpaper.url))
        }
    }

    private fun getImageLoadCallback(behavior: Behavior): ImageLoader.Callback {
        return object : ImageLoader.Callback {
            override fun onFinish() {
                isLoading = false
            }

            @SuppressLint("MissingPermission")
            override fun onSuccess(image: File?) {
                callback?.onLoadOrigin(pos, true)
                when (behavior) {
                    Behavior.SET_WALLPAPER -> {
                        image?.absolutePath?.let { HkUtils.setWallpaper(mContext, it) }
                    }
                    Behavior.LOAD_NORMAL -> {
                        normalFile = image
                    }
                    Behavior.LOAD_ORIGIN -> {
                        originFile = image
                    }
                    Behavior.ONLY_DOWNLOAD_ORIGIN -> {
                        savePicture(wallpaper.originUrl, image)
                    }
                    Behavior.ONLY_DOWNLOAD_NORMAL -> {
                        savePicture(wallpaper.url, image)
                    }
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
                isLoading = true
            }
        }
    }

    fun downLoadPicture(type: SaveType) {
        val destUrl: String
        val curFile: File?
        if (type == SaveType.NORMAL) {
            if (normalFile == null) {
                loadPicture(Behavior.ONLY_DOWNLOAD_NORMAL)
                return
            }
            destUrl = wallpaper.url
            curFile = normalFile
        } else {
            if (originFile == null) {
                loadPicture(Behavior.ONLY_DOWNLOAD_ORIGIN)
                return
            }
            destUrl = wallpaper.originUrl
            curFile = originFile
        }
        savePicture(destUrl, curFile)
    }

    /**
     * 兼容Android10的沙盒下载
     * */
    fun savePicture(destUrl: String, curFile: File?) {
        Timber.d("save picture url %s", destUrl)
        val destPath = PicUtils.getInstance().getDownloadPicturePath(mContext, destUrl)
        if (false) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
            values.put(MediaStore.Images.Media.DISPLAY_NAME, URLUtil.guessFileName(destUrl
                    , null, null))
            values.put(MediaStore.Images.ImageColumns.IS_PENDING, true)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + "萌幻Cos")
            val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri == null) {
                ToastUtils.showShort("下载失败 uri null")
                return
            }
            val ous: ParcelFileDescriptor? = context?.contentResolver?.openFileDescriptor(AndPermission.getFileUri(context, File(destPath)), "rw")
            val ins = context?.contentResolver?.openFileDescriptor(AndPermission.getFileUri(context, curFile), "rw")
            if (ins == null || ous == null) {
                ToastUtils.showShort("下载失败 ins==null||ous==null ")
                return
            }
            android.os.FileUtils.copy(ins.fileDescriptor, ous.fileDescriptor)
            values.clear()
            values.put(MediaStore.Images.ImageColumns.IS_PENDING, false)
            uri.let { context?.contentResolver?.update(it, values, null, null) }
        } else {
            val created = FileUtils.createOrExistsFile(destPath)
            if (!created) {
                ToastUtils.showShort("创建文件失败")
                return
            }
            val copy = FileUtils.copyFile(curFile, File(destPath))
            if (!copy) {
                ToastUtils.showShort("生成文件失败")
                return
            }
            MediaStore.Images.Media.insertImage(context?.contentResolver,
                    destPath, URLUtil.guessFileName(destUrl, null, null), "")
        }
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