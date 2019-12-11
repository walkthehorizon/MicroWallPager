package com.shentu.wallpaper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.jess.arms.base.BaseActivity
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.BuildConfig
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.di.component.DaggerMyComponent
import com.shentu.wallpaper.di.module.MyModule
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.AppUpdate
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.mvp.contract.MyContract
import com.shentu.wallpaper.mvp.presenter.MyPresenter
import com.shentu.wallpaper.mvp.ui.activity.SettingMoreActivity
import com.shentu.wallpaper.mvp.ui.login.LoginActivity
import com.shentu.wallpaper.mvp.ui.my.MyEditActivity
import com.shentu.wallpaper.mvp.ui.update.AppUpdateDialog
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_my.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber


class TabMyFragment : BaseFragment<MyPresenter>(), MyContract.View {

    companion object {
        fun newInstance(): TabMyFragment {
            return TabMyFragment()
        }
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerMyComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myModule(MyModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_my, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        refreshUser()
        rlHead.setOnClickListener { clickHead() }
        itCollect.setOnClickListener { clickCollect() }
        itUpdate.setEndValue(BuildConfig.VERSION_NAME)
        itUpdate.setOnClickListener {
            checkUpdate(true)
        }
        itCache.setOnClickListener { clickCache() }
        itFeedback.setOnClickListener { clickFeedback() }
        itMore.setOnClickListener {
            startActivity(Intent(mContext, SettingMoreActivity::class.java))
        }
        rlHead.setOnClickListener {
            if (!HkUserManager.instance.isLogin) {
                LoginActivity.open()
                return@setOnClickListener
            }
            startActivity(Intent(mContext, MyEditActivity::class.java))
        }
        itMoney.setOnClickListener {
            if (!HkUserManager.instance.isLogin) {
                launchActivity(Intent(mContext, LoginActivity::class.java))
                return@setOnClickListener
            }
        }
        if (!TimeUtils.isToday(SPUtils.getInstance().getLong(Constant.LAST_NOTIFY_TIME))) {
            checkUpdate(false)
        }

    }

    private fun checkUpdate(isUser: Boolean) {
//        ToastUtils.showShort("请求版本信息啦")
        ArmsUtils.obtainAppComponentFromContext(mContext)
                .repositoryManager()
                .obtainRetrofitService(MicroService::class.java)
                .updateInfo
                .compose(RxUtils.applyClearSchedulers(this))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<AppUpdate>>(
                        ArmsUtils.obtainAppComponentFromContext(mContext).rxErrorHandler()) {
                    override fun onNext(response: BaseResponse<AppUpdate>) {
                        if (!response.isSuccess) {
                            return
                        }
                        if (response.data!!.versionCode > BuildConfig.VERSION_CODE) {
                            itUpdate.setEndValue("有新版本：${response.data.versionName}")
                            showUpdateDialog(response.data)
                        }
                        if (isUser) {
                            ToastUtils.showShort("已是最新版本")
                        }
                    }
                })
    }

    private fun showUpdateDialog(update: AppUpdate) {
        val appUpdateDialog = AppUpdateDialog.newInstance(update)
        appUpdateDialog.show((activity as BaseActivity<*>).supportFragmentManager
                , AppUpdateDialog::class.java.simpleName)
    }

    private fun refreshUser() {
        val user = HkUserManager.instance.user
        if (HkUserManager.instance.isLogin) {
            tvMyName.text = user.nickname
            ivSex.setImageResource(when (user.sex) {
                1 -> R.drawable.ic_im_sex_man
                2 -> R.drawable.ic_im_sex_woman
                else -> R.drawable.ic_im_sex_unsure
            })
            if (user.sex == 2) {
                ivSex.setColorFilter(ContextCompat.getColor(mContext, R.color.red_trans))
            } else {
                ivSex.clearColorFilter()
            }
            GlideArms.with(this)
                    .load(HkUserManager.instance.user.avatar)
                    .into(circle_avatar)
            itMoney.setEndValue(user.pea.toString())
        } else {
            tvMyName.text = "微梦用户"
            GlideArms.with(this)
                    .load(R.drawable.default_head)
                    .into(circle_avatar)
            itMoney.setEndValue("")
        }
        itCache.setEndValue(FileUtils.getDirSize(ArmsUtils.obtainAppComponentFromContext(mContext).cacheFile()))
    }

    override fun onResume() {
        super.onResume()
        refreshUser()
    }

    private fun clickHead() {
        if (!HkUserManager.instance.isLogin) {
            launchActivity(Intent(mContext, LoginActivity::class.java))
            return
        }
    }

    private fun clickCollect() {
        if (!HkUserManager.instance.isLogin) {
            launchActivity(Intent(mContext, LoginActivity::class.java))
            return
        }
        ARouter.getInstance().build("/activity/my/collect/")
                .navigation(mContext)
    }

    /**
     * 直接通过qq反馈
     * */
    private fun clickFeedback() {
        HkUtils.contactKefu()
    }

    private fun clickCache() {
        Completable.fromAction { GlideArms.get(mContext).clearDiskCache() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    GlideArms.get(mContext).clearMemory()
                    if (BuildConfig.DEBUG) {
                        ArmsUtils.obtainAppComponentFromContext(mContext)
                                .repositoryManager()
                                .clearAllCache()
                    }
                    itCache.setEndValue(FileUtils.getDirSize(ArmsUtils.obtainAppComponentFromContext(mContext).cacheFile()))
                    ToastUtils.showShort("清理完成")
                }
                .subscribe()
    }

    override fun setData(data: Any?) {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }
}
