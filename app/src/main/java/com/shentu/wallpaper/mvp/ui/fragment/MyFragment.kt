package com.shentu.wallpaper.mvp.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.event.LoginSuccessEvent
import com.shentu.wallpaper.app.event.LogoutEvent
import com.shentu.wallpaper.di.component.DaggerMyComponent
import com.shentu.wallpaper.di.module.MyModule
import com.shentu.wallpaper.mvp.contract.MyContract
import com.shentu.wallpaper.mvp.presenter.MyPresenter
import com.shentu.wallpaper.mvp.ui.activity.SettingMoreActivity
import com.tencent.bugly.beta.Beta
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_my.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


class MyFragment : BaseFragment<MyPresenter>(), MyContract.View {
    companion object {
        fun newInstance(): MyFragment {
            return MyFragment()
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
        return inflater.inflate(com.shentu.wallpaper.R.layout.fragment_my, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        if (HkUserManager.getInstance().isLogined) {
            refreshUser()
        }
        if (Beta.getUpgradeInfo() != null) {
            itUpdate.setEndValue("有新的升级可用")
        }
        rlHead.setOnClickListener { clickHead() }
        itCollect.setOnClickListener { clickCollect() }
        itBrowser.setOnClickListener { clickBrowser() }
        itUpdate.setOnClickListener {
            Beta.checkUpgrade()
        }
        itCache.setOnClickListener { clickCache() }
        itFeedback.setOnClickListener { clickFeedback() }
        itMore.setOnClickListener {
            startActivity(Intent(context, SettingMoreActivity::class.java))
        }
    }

    private fun refreshUser() {
        tvMyName.text = HkUserManager.getInstance().user.nickname
        GlideArms.with(this)
                .load(HkUserManager.getInstance().user.avatar)
                .into(circle_avatar)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateUser(event: LoginSuccessEvent) {
        refreshUser()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateUser(event: LogoutEvent) {
        refreshUser()
    }

    fun clickHead() {
        HkUserManager.getInstance().checkLogin(context)
    }

    fun clickCollect() {
        HkUserManager.getInstance().checkLogin(context)
        //激活直接显示的方式
    }

    fun clickBrowser() {
        ToastUtils.showShort("待完善")

    }

    /**
     * 直接通过qq反馈
     * */
    private fun clickFeedback() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=160585515")))
        } catch (e: ClassNotFoundException) {
            Timber.e(e)
        }
    }

    private fun clickCache() {
        Completable.fromAction { GlideArms.get(mContext).clearMemory() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe()
        Completable.fromAction {
            GlideArms.get(mContext).clearDiskCache()
        }.subscribeOn(Schedulers.io())
                .doOnComplete {
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
