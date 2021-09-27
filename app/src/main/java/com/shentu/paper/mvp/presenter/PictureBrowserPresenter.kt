//package com.shentu.paper.mvp.presenter
//
//import android.app.Application
//import android.content.Context
//import com.blankj.utilcode.util.FileUtils
//import com.blankj.utilcode.util.ToastUtils
//
//import com.micro.mvp.BasePresenter
//import com.shentu.paper.app.GlideApp
//import com.shentu.paper.app.HkUserManager
//import com.shentu.paper.app.utils.RxUtils
//import com.shentu.paper.model.entity.Wallpaper
//import com.shentu.paper.model.response.BaseResponse
//import com.shentu.paper.model.response.WallpaperPageResponse
//import com.shentu.paper.mvp.contract.PictureBrowserContract
//import com.shentu.paper.mvp.ui.browser.SaveType
//import io.reactivex.Observable
//import io.reactivex.ObservableOnSubscribe
//import me.jessyan.rxerrorhandler.core.RxErrorHandler
//import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
//import timber.log.Timber
//import java.io.File
//import javax.inject.Inject
//
//
//class PictureBrowserPresenter
//@Inject
//constructor(model: PictureBrowserContract.Model, rootView: PictureBrowserContract.View) :
//        BasePresenter<PictureBrowserContract.Model, PictureBrowserContract.View>(model, rootView) {
//    @Inject
//    lateinit var mErrorHandler: RxErrorHandler
//
//    @Inject
//    lateinit var mApplication: Application
//
//
//
//    fun addCollect(pid: Long, position: Int) {
//        mModel.addCollect(pid)
//                .compose(RxUtils.applyClearSchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Boolean>>(mErrorHandler) {
//                    override fun onNext(t: BaseResponse<Boolean>) {
//                        if (!t.isSuccess) {
//                            mRootView.resetCollect()
//                            return
//                        }
//                        mRootView.showLikeStatus(position)
//                    }
//
//                    override fun onError(t: Throwable) {
//                        super.onError(t)
//                        mRootView.resetCollect()
//                    }
//                })
//    }
//
//    fun getPictures(id: Int) {
//        mModel.getWallPapersBySubjectId(id)
//                .compose(RxUtils.applySchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(mErrorHandler) {
//                    override fun onNext(t: WallpaperPageResponse) {
//                        t.data?.content?.let { it -> mRootView.showPictures(it) }
////                        t.data?.content?.let { checkPictureOriginExists(it) }
//                    }
//
//                    override fun onError(t: Throwable) {
//                        super.onError(t)
//                        Timber.e(t.message)
//                    }
//                })
//    }
//
//    fun checkPictureOriginExists(wallpapers: MutableList<Wallpaper>) {
//        Observable.create(ObservableOnSubscribe<MutableList<Wallpaper>> {
//            for (wallpaper in wallpapers) {
//                val file: File? = try {
//                    GlideApp.with(mRootView as Context).downloadOnly().load(wallpaper.originUrl).onlyRetrieveFromCache(true).submit().get()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    null
//                }
//                wallpaper.isOriginExist = FileUtils.isFileExists(file)
//            }
//            it.onNext(wallpapers)
//            it.onComplete()
//        }).compose(RxUtils.applyClearSchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<MutableList<Wallpaper>>(mErrorHandler) {
//                    override fun onNext(t: MutableList<Wallpaper>) {
//                        mRootView.showPictures(t)
//                    }
//                })
//    }
//
//    fun buyPaper(position: Int, wallpaper: Wallpaper, type: SaveType) {
//        mModel.buyPaper(wallpaper.id, type.value)
//                .compose(RxUtils.applyClearSchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Int>>(mErrorHandler) {
//                    override fun onNext(t: BaseResponse<Int>) {
//                        if (t.code == 1401) {//已购
//                            mRootView.savePicture(position, type)
//                            return
//                        }
//                        if (t.code == 1402) {//看豆不足
//                            ToastUtils.showLong("很抱歉，萌豆不足！")
//                            return
//                        }
//                        if (!t.isSuccess) {
//                            return
//                        }
//                        HkUserManager.updateKandou(-t.data!!)
//                        mRootView.savePicture(position, type)
//                    }
//                })
//    }
//
//    fun updateCategoryCover(cid: Int, logo: String) {
//        mModel.updateCategoryCover(cid, logo)
//                .compose(RxUtils.applySchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Boolean>>(mErrorHandler) {
//                    override fun onNext(t: BaseResponse<Boolean>) {
//                        if (!t.isSuccess) {
//                            return
//                        }
//                        ToastUtils.showShort("修改成功!")
//                    }
//                })
//    }
//
//    fun addPaper2Banner(bid: Int, pid: Long) {
//        mModel.addPaper2Banner(bid, pid)
//                .compose(RxUtils.applyClearSchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Boolean>>(mErrorHandler) {
//                    override fun onNext(t: BaseResponse<Boolean>) {
//                        if (!t.isSuccess) {
//                            return
//                        }
//                        ToastUtils.showShort("修改成功!")
//                    }
//                })
//    }
//
//    fun getShareData(paper: Wallpaper) {
//        mModel.getPaperDetail(paper.id)
//                .compose(RxUtils.applyClearSchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Wallpaper>>(mErrorHandler) {
//                    override fun onNext(t: BaseResponse<Wallpaper>) {
//                        t.data?.let { mRootView.showShare(it) }
//                    }
//
//                    override fun onError(t: Throwable) {
//                        mRootView.showMessage("生成分享数据异常")
//                    }
//                })
//    }
//
//    fun getPaperDetail(paperId: Long) {
//        mModel.getPaperDetail(paperId)
//                .compose(RxUtils.applyClearSchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Wallpaper>>(mErrorHandler) {
//                    override fun onNext(t: BaseResponse<Wallpaper>) {
//                        t.data?.let { mRootView.showPictures(mutableListOf(t.data)) }
//                    }
//                })
//    }
//
//    fun setGarbage(paperId: Long) {
//        mModel.setGarbage(paperId)
//                .compose(RxUtils.applyClearSchedulers(mRootView))
//                .subscribe(object : ErrorHandleSubscriber<BaseResponse<String>>(mErrorHandler) {
//                    override fun onNext(t: BaseResponse<String>) {
//                        if (t.isSuccess) {
//                            ToastUtils.showShort("感谢您的反馈")
//                        }
//                    }
//                })
//    }
//}
