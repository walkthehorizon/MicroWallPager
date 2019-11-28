package com.shentu.wallpaper.mvp.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import butterknife.BindView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.SPUtils
import com.bumptech.glide.Glide
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.BuildConfig
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.di.component.DaggerSettingMoreComponent
import com.shentu.wallpaper.di.module.SettingMoreModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.SettingMoreContract
import com.shentu.wallpaper.mvp.presenter.SettingMorePresenter
import com.shentu.wallpaper.mvp.ui.browser.SaveType
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_setting_more.*
import kotlinx.android.synthetic.main.fragment_picture_browser.*
import timber.log.Timber
import kotlin.math.min


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
        mIvCover.post {
            mIvCover.setImageBitmap(HkUtils.instance.getSvgBitmap(mIvCover.width,mIvCover.height
                    ,R.drawable.ic_launcher,R.drawable.ic_favorite_black_24dp))
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
