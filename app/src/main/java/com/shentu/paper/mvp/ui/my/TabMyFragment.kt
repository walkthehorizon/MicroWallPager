package com.shentu.paper.mvp.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.MultiChoiceListener
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.reflect.TypeToken
import com.jess.arms.base.BaseActivity
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.paper.BuildConfig
import com.shentu.paper.R
import com.shentu.paper.app.Constant
import com.shentu.paper.app.GlideArms
import com.shentu.paper.app.GlideConfiguration
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.utils.HkUtils
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.databinding.FragmentMyBinding
import com.shentu.paper.di.component.DaggerMyComponent
import com.shentu.paper.di.module.MyModule
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.entity.AppUpdate
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.mvp.contract.MyContract
import com.shentu.paper.mvp.presenter.MyPresenter
import com.shentu.paper.mvp.ui.activity.SettingMoreActivity
import com.shentu.paper.mvp.ui.login.LoginActivity
import com.shentu.paper.mvp.ui.update.AppUpdateDialog
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_my.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import java.io.File


class TabMyFragment : BaseFragment<MyPresenter>(), MyContract.View {

    private lateinit var binding: FragmentMyBinding
    private lateinit var initModes: List<String>

    companion object {
        fun newInstance(): TabMyFragment {
            return TabMyFragment()
        }
    }

    private lateinit var glideCache: File

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerMyComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myModule(MyModule(this))
                .build()
                .inject(this)
        glideCache = File(appComponent.cacheFile(), GlideConfiguration.IMAGE_DISK_CACHE_PATH)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyBinding.inflate(layoutInflater)
        return binding.root
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
        val modeSp = SPUtils.getInstance().getString(Constant.CONTENT_MODE, "")
        initModes = if (modeSp.isEmpty()) {
            listOf(ContentMode.ANIM.name)
        } else {
            ArmsUtils.obtainAppComponentFromContext(mContext).gson()
                    .fromJson(modeSp, object : TypeToken<List<String>>() {}.type)
        }
        binding.itMode.setEndValue(ContentMode.getModeStr(initModes))
        binding.itMode.setOnClickListener {
            if (!HkUserManager.isLogin) {
                launchActivity(Intent(mContext, LoginActivity::class.java))
                return@setOnClickListener
            }
            showContentModeDialog()
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


    private fun showContentModeDialog() {
        val initSelects = IntArray(initModes.size)
        if (initModes.contains(ContentMode.ANIM.name)) {
            initSelects[0] = 0
        }
        if (initModes.contains(ContentMode.COS.name)) {
            initSelects[1] = 1
        }
        var selectModes: List<String> = emptyList()
        MaterialDialog(mContext)
                .listItemsMultiChoice(initialSelection = initSelects, items = listOf(ContentMode.ANIM.name, ContentMode.COS.name), selection = object : MultiChoiceListener {
                    override fun invoke(dialog: MaterialDialog, indices: IntArray, items: List<String>) {
                        selectModes = items
                    }
                })
                .positiveButton {
                    initModes = selectModes
                    SPUtils.getInstance().put(Constant.CONTENT_MODE, ArmsUtils.obtainAppComponentFromContext(mContext)
                            .gson().toJson(selectModes))
                    binding.itMode.setEndValue(ContentMode.getModeStr(selectModes))
                    //TODO 刷新数据
                }
                .show()
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
            GlideArms.with(this)
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
            GlideArms.with(this)
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
        Completable.fromAction { GlideArms.get(mContext).clearDiskCache() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    GlideArms.get(mContext).clearMemory()
                    itCache.setEndValue(FileUtils.getDirSize(glideCache))
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
