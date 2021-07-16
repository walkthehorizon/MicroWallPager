package com.shentu.paper.mvp.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseActivity
import com.jess.arms.utils.ArmsUtils
import com.shentu.paper.R
import com.shentu.paper.app.Constant
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.mvp.contract.SettingMoreContract
import com.shentu.paper.mvp.presenter.SettingMorePresenter
import kotlinx.android.synthetic.main.activity_setting_more.*


class SettingMoreActivity : BaseActivity<SettingMorePresenter>(), SettingMoreContract.View {


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_setting_more
    }


    override fun initData(savedInstanceState: Bundle?) {
//        if (HkUserManager.instance.isLogin) {
//            mbLogout.visibility = View.VISIBLE
//            mbLogout.setOnClickListener {
//                mPresenter?.logout()
//            }
//        } else {
//            mbLogout.visibility = View.GONE
//        }
        val type = SPUtils.getInstance().getInt(Constant.DOWNLOAD_TYPE, 0)
        rivDownload.setEndValue("")
        rivDownload.setOnClickListener {
            showDownloadDialog(type)
        }
//        mIvCover.post {
//            mIvCover.setImageBitmap(HkUtils.instance.getSvgBitmap(mIvCover.width, mIvCover.height
//                    , R.drawable.ic_launcher, R.drawable.ic_favorite_black_24dp))
//        }
        if (HkUserManager.user.canSetMode) {
            rivAbout.setOnClickListener {
                BrowserActivity.open(this, Constant.GITHUB_URL)
            }
        } else {
            rivAbout.visibility = View.GONE
        }
        rivServer.setOnClickListener {
            BrowserActivity.open(this, Constant.WEB_SERVER)
        }
        rivPrivacy.setOnClickListener {
            BrowserActivity.open(this, Constant.WEB_PRIVACY)
        }
        rivEmail.setOnClickListener {
            val cm: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData: ClipData = ClipData.newPlainText("Label", rivEmail.getEndValue())
            cm.setPrimaryClip(mClipData)
            ToastUtils.showShort("已复制到粘贴板")
        }
    }

    private fun showDownloadDialog(type: Int) {
        MaterialDialog(this)
                .title(text = "图片清晰度选择")
                .listItemsSingleChoice(items = listOf("每次询问", "高清", "原图")
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
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }
}
