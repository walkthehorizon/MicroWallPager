package com.shentu.paper.mvp.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.SingleChoiceListener
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseActivity
import com.jess.arms.base.BaseFragment
import com.jess.arms.integration.RepositoryManager
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.ArmsUtils
import com.shentu.paper.BuildConfig
import com.shentu.paper.R
import com.shentu.paper.app.Constant
import com.shentu.paper.app.GlideApp
import com.shentu.paper.app.GlideConfiguration
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.config.Config
import com.shentu.paper.app.utils.HkUtils
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.databinding.FragmentMyBinding
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.entity.AppUpdate
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.mvp.contract.MyContract
import com.shentu.paper.mvp.presenter.MainPresenter
import com.shentu.paper.mvp.ui.activity.SettingMoreActivity
import com.shentu.paper.mvp.ui.login.LoginActivity
import com.shentu.paper.mvp.ui.update.AppUpdateDialog
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_my.*
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class TabMyFragment : BaseFragment<MainPresenter>(), MyContract.View {

    private lateinit var binding: FragmentMyBinding

    @Inject
    lateinit var repositoryManager: RepositoryManager

    @Inject
    lateinit var errorHandler: RxErrorHandler

    companion object {
        fun newInstance(): TabMyFragment {
            return TabMyFragment()
        }
    }

    private lateinit var glideCache: File

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initData(savedInstanceState: Bundle?) {
        glideCache = File(Config.cachePath, GlideConfiguration.IMAGE_DISK_CACHE_PATH)
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
            if (!HkUserManager.isLogin) {
                LoginActivity.open()
                return@setOnClickListener
            }
            startActivity(Intent(mContext, MyEditActivity::class.java))
        }
        itMoney.setOnClickListener {
            if (!HkUserManager.isLogin) {
                launchActivity(Intent(mContext, LoginActivity::class.java))
                return@setOnClickListener
            }
        }
        if (HkUserManager.user.canSetMode) {
            binding.itMode.visibility = View.VISIBLE
            val curMode = SPUtils.getInstance().getInt(Constant.CONTENT_MODE, 0)
            binding.itMode.setEndValue(ContentMode.getContentMode(curMode).name)
            binding.itMode.setOnClickListener {
                if (!HkUserManager.isLogin) {
                    launchActivity(Intent(mContext, LoginActivity::class.java))
                    return@setOnClickListener
                }
                showContentModeDialog()
            }
        }
        if (!TimeUtils.isToday(SPUtils.getInstance().getLong(Constant.LAST_NOTIFY_TIME))) {
            checkUpdate(false)
        }
    }

    private fun checkUpdate(isUser: Boolean) {
        repositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .updateInfo
                .compose(RxUtils.applyClearSchedulers(this))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<AppUpdate>>(errorHandler) {
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


    private fun showContentModeDialog() {
        val curMode = SPUtils.getInstance().getInt(Constant.CONTENT_MODE, HkUserManager.user.defaultContentMode)
        val modes = listOf(ContentMode.COS.tName, ContentMode.ANIM.tName)
        MaterialDialog(requireContext())
                .listItemsSingleChoice(initialSelection = curMode, items = modes, selection = object : SingleChoiceListener {
                    override fun invoke(dialog: MaterialDialog, index: Int, text: String) {
                        if(index ==curMode){
                            return
                        }
                        SPUtils.getInstance().put(Constant.CONTENT_MODE, ContentMode.getContentMode(index).id)
                        binding.itMode.setEndValue(modes[index])
                        EventBus.getDefault().post(ContentMode.getContentMode(index))
                    }
                }).show()
    }

    private fun showUpdateDialog(update: AppUpdate) {
        val appUpdateDialog = AppUpdateDialog.newInstance(update)
        appUpdateDialog.show((activity as BaseActivity<*>).supportFragmentManager
                , AppUpdateDialog::class.java.simpleName)
    }

    private fun refreshUser() {
        val user = HkUserManager.user
        if (HkUserManager.isLogin) {
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
            GlideApp.with(this)
                    .load(HkUserManager.user.avatar)
                    .into(circle_avatar)
            itMoney.setEndValue(user.pea.toString())
            when {
                HkUserManager.user.vip -> {
                    tvIdentify.text = "VIP"
                    tvIdentify.setTextColor(ContextCompat.getColor(mContext, R.color.red_dark))
                }
//                HkUserManager.instance.user.svip -> {
//                    tvIdentify.text = "SVIP"
//                    tvIdentify.setTextColor(ContextCompat.getColor(mContext, R.color.yellow_dark))
//                }
                else -> {
                    tvIdentify.text = ""
                }
            }
        } else {
            tvMyName.text = "微梦用户"
            GlideApp.with(this)
                    .load(R.drawable.default_head)
                    .into(circle_avatar)
            itMoney.setEndValue("")
        }
        itCache.setEndValue(FileUtils.getDirSize(glideCache))
    }

    override fun onResume() {
        super.onResume()
        refreshUser()
    }

    private fun clickHead() {
        if (!HkUserManager.isLogin) {
            launchActivity(Intent(mContext, LoginActivity::class.java))
            return
        }
    }

    private fun clickCollect() {
        if (!HkUserManager.isLogin) {
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
        Completable.fromAction { GlideApp.get(mContext).clearDiskCache() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    GlideApp.get(mContext).clearMemory()
                    itCache.setEndValue(FileUtils.getDirSize(glideCache))
                    ToastUtils.showShort("清理完成")
                }
                .subscribe()
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }
}
