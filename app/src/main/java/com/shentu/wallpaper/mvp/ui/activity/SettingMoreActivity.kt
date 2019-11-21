package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import butterknife.BindView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.SPUtils
import com.bumptech.glide.Glide
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.BuildConfig
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.di.component.DaggerSettingMoreComponent
import com.shentu.wallpaper.di.module.SettingMoreModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.SettingMoreContract
import com.shentu.wallpaper.mvp.presenter.SettingMorePresenter
import com.shentu.wallpaper.mvp.ui.browser.SaveType
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_setting_more.*
import kotlinx.android.synthetic.main.fragment_picture_browser.*


class SettingMoreActivity : BaseActivity<SettingMorePresenter>(), SettingMoreContract.View {

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerSettingMoreComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .settingMoreModule(SettingMoreModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_setting_more
    }


    override fun initData(savedInstanceState: Bundle?) {
        Glide.with(this)
                .load(R.drawable.ic_launcher)
                .transform(RoundedCornersTransformation(ConvertUtils.dp2px(12.0f), 0, RoundedCornersTransformation.CornerType.ALL))
                .into(mIvCover)
        mTvVersion.text = BuildConfig.VERSION_NAME
        if (HkUserManager.getInstance().isLogin) {
            mbLogout.visibility = View.VISIBLE
            mbLogout.setOnClickListener {
                mPresenter?.logout()
            }
        } else {
            mbLogout.visibility = View.GONE
        }
        val type = SPUtils.getInstance().getInt(Constant.DOWNLOAD_TYPE, 0)
        rivDownload.setEndValue("")
        rivDownload.setOnClickListener {
            showDownloadDialog(type)
        }
    }

    private fun showDownloadDialog(type: Int) {
        MaterialDialog(this)
                .title(text = "下载")
                .listItemsSingleChoice(items = listOf("询问", "默认", "原图")
                        , initialSelection = type) { _, index, _ ->
                    SPUtils.getInstance().put(Constant.DOWNLOAD_TYPE, index)
                }
                .show()
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
        finish()
    }
}
