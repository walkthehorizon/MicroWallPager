package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.FileUtils
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadSampleListener
import com.liulishuo.filedownloader.FileDownloader
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.app.utils.PicUtils
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@ActivityScope
class PictureBrowserPresenter
@Inject
constructor(model: PictureBrowserContract.Model, rootView: PictureBrowserContract.View) :
        BasePresenter<PictureBrowserContract.Model, PictureBrowserContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager
//    @Inject
//    lateinit var context: Context


    fun getPictures(id: Int) {
        mModel.getWallPapersBySubjectId(id)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
                    override fun onNext(t: WallpaperPageResponse) {
                        t.data?.content?.let { checkPictureOriginExists(it) }
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        Timber.e(t.message)
                    }
                })
    }

    fun checkPictureOriginExists(wallpapers: MutableList<Wallpaper>) {
        Observable.create(ObservableOnSubscribe<MutableList<Wallpaper>> {
            for (wallpaper in wallpapers) {
                val file: File? = try {
                    GlideArms.with(mRootView as Context).downloadOnly().load(wallpaper.origin_url).onlyRetrieveFromCache(true).submit().get()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                wallpaper.isOriginExist = FileUtils.isFileExists(file)
            }
            it.onNext(wallpapers)
            it.onComplete()
        }).compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<MutableList<Wallpaper>>(mErrorHandler) {
                    override fun onNext(t: MutableList<Wallpaper>) {
                        mRootView.showPictures(t)
                    }
                })
    }

    fun downloadPicture(pictureUrl: String) {
        FileDownloader.getImpl().create(pictureUrl)
                .setPath(PicUtils.getInstance().getDownloadPicturePath(pictureUrl))
                .setListener(object : FileDownloadSampleListener() {
                    override fun completed(task: BaseDownloadTask?) {
                        super.completed(task)
                        mRootView.showMessage("图片已保存在 手机相册》看个够")
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        super.error(task, e)
                        mRootView.showMessage("下载异常：" + e?.message)
                    }
                })
                .start()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
