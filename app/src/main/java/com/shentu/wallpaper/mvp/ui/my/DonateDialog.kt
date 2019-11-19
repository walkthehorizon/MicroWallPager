package com.shentu.wallpaper.mvp.ui.my

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.utils.HkUtils
import kotlinx.android.synthetic.main.activity_donate.*
import java.util.jar.Attributes

class DonateDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_donate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = dialog?.window?.attributes
        lp?.height = -2
        lp?.width = -2
        dialog?.window?.attributes = lp
        lottieDonate.playAnimation()
        mbDonate.setOnClickListener {
            context?.let { it1 -> HkUtils.instance.showChargeDialog(it1) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lottieDonate.cancelAnimation()
    }
}
