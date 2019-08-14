package com.shentu.wallpaper.mvp.ui.update

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.SPUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.model.entity.AppUpdate
import kotlinx.android.synthetic.main.dialog_update.*

class AppUpdateDialog : DialogFragment() {

    private var update: AppUpdate? = null
    private var appDownloadManager: AppDownloadManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_update, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update = arguments?.getSerializable("update") as AppUpdate
        if (update != null) {
            tvVersion!!.text = "V" + update!!.versionName!!
            tvContent!!.text = update!!.updateInfo
        }
        mbUpdate.setOnClickListener { clickUpdate() }
        ivClose.setOnClickListener {
            SPUtils.getInstance().put(Constant.LAST_NOTIFY_TIME, System.currentTimeMillis())
            dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isCancelable = false
        if (dialog!!.window == null) {
            return
        }
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun clickUpdate() {
        if (appDownloadManager == null) {
            appDownloadManager = AppDownloadManager(this.activity!!)
            activity!!.lifecycle.addObserver(appDownloadManager!!)
        }
        appDownloadManager!!.downloadApk(update?.appUrl!!, resources.getString(R.string.app_name)
                , update?.versionName!!)
        dismiss()
    }

    companion object {
        fun newInstance(update: AppUpdate): AppUpdateDialog {
            val args = Bundle()
            args.putSerializable("update", update)
            val fragment = AppUpdateDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
