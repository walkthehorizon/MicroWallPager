package com.shentu.wallpaper.mvp.presenter

import android.app.Application
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.event.LogoutEvent
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.entity.BaseResponse
import com.shentu.wallpaper.mvp.contract.SettingMoreContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@ActivityScope
class SettingMorePresenter
@Inject
constructor(model: SettingMoreContract.Model, rootView: SettingMoreContract.View) :
        BasePresenter<SettingMoreContract.Model, SettingMoreContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager


    fun logout() {
        mModel.logout()
                .compose(RxUtils.applySchedulers(mRootView))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<String>>(mErrorHandler) {
                    override fun onNext(t: BaseResponse<String>) {
                        if (!t.isSuccess) {
                            return
                        }
                        HkUserManager.getInstance().clear()
                        EventBus.getDefault().post(LogoutEvent())
                        mRootView.killMyself()
                    }
                })
    }
}
