package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.shentu.wallpaper.mvp.ui.browser.SaveType
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
    lateinit var mAppManager: AppManager

    fun addCollect(pid: Int, position: Int) {
        mModel.addCollect(pid)
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Boolean>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<Boolean>) {
                        if (!t.isSuccess) {
                            mRootView.resetCollect()
                            return
                        }
                        mRootView.showCollectAnim(position)
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        mRootView.resetCollect()
                    }
                })
    }

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
                    GlideArms.with(mRootView as Context).downloadOnly().load(wallpaper.originUrl).onlyRetrieveFromCache(true).submit().get()
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

    fun buyPaper(position: Int, wallpaper: Wallpaper, pea: Int) {
        mModel.buyPaper(wallpaper.id, pea)
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<String>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<String>) {
                        if (!t.isSuccess) {
                            return
                        }
                        mRootView.savePicture(position, if (pea == 3) SaveType.ORIGIN else SaveType.NORMAL)
                        //downloadPicture(if (pea == 3) wallpaper.originUrl else wallpaper.url)
                    }
                })
    }

    fun updateCategoryCover(cid: Int, logo: String) {
        mModel.updateCategoryCover(cid, logo)
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Boolean>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<Boolean>) {
                        if (!t.isSuccess) {
                            return
                        }
                        ToastUtils.showShort("修改成功!")
                    }
                })
    }

    fun getShareData(paper: Wallpaper) {
        mModel.getPaperDetail(paper.id)
                .compose(RxUtils.applyClearSchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Wallpaper>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<Wallpaper>) {
                        t.data?.let { mRootView.showShare(it) }
                    }

                    override fun onError(t: Throwable) {
                        mRootView.showMessage("生成分享数据异常")
                    }
                })

    }

}
