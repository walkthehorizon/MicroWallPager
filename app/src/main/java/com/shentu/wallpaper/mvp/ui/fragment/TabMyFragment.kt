package com.shentu.wallpaper.mvp.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.BuildConfig
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.di.component.DaggerMyComponent
import com.shentu.wallpaper.di.module.MyModule
import com.shentu.wallpaper.mvp.contract.MyContract
import com.shentu.wallpaper.mvp.presenter.MyPresenter
import com.shentu.wallpaper.mvp.ui.activity.SettingMoreActivity
import com.shentu.wallpaper.mvp.ui.login.LoginActivity
import com.shentu.wallpaper.mvp.ui.my.MyEditActivity
import com.tencent.bugly.beta.Beta
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_my.*
import timber.log.Timber


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
        if (Beta.getUpgradeInfo() != null) {
            itUpdate.setEndValue("有新的升级")
        } else {
            itUpdate.setEndValue(BuildConfig.VERSION_NAME)
        }
        rlHead.setOnClickListener { clickHead() }
        itCollect.setOnClickListener { clickCollect() }
        itUpdate.setOnClickListener {
            Beta.checkUpgrade()
        }
        itCache.setOnClickListener { clickCache() }
        itFeedback.setOnClickListener { clickFeedback() }
        itMore.setOnClickListener {
            startActivity(Intent(mContext, SettingMoreActivity::class.java))
        }
        rlHead.setOnClickListener {
            if (!HkUserManager.getInstance().isLogin) {
                LoginActivity.open()
                return@setOnClickListener
            }
            startActivity(Intent(mContext, MyEditActivity::class.java))
        }
    }

    private fun refreshUser() {
        val user = HkUserManager.getInstance().user
        if (HkUserManager.getInstance().isLogin) {
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
                    .load(HkUserManager.getInstance().user.avatar)
                    .into(circle_avatar)
        } else {
            tvMyName.text = "微梦用户"
            GlideArms.with(this)
                    .load(R.drawable.default_head)
                    .into(circle_avatar)
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun updateUser(event: LoginSuccessEvent) {
//        refreshUser()
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun updateUser(event: LogoutEvent) {
//        refreshUser()
//    }

    override fun onResume() {
        super.onResume()
        refreshUser()
    }

    private fun clickHead() {
        if (!HkUserManager.getInstance().isLogin) {
            launchActivity(Intent(mContext, LoginActivity::class.java))
            return
        }
    }

    private fun clickCollect() {
        if (!HkUserManager.getInstance().isLogin) {
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
        ArmsUtils.obtainAppComponentFromContext(mContext)
                .repositoryManager()
                .clearAllCache()
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
