package com.shentu.paper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.navigation.NavigationBarView
import com.micro.integration.AppManager
import com.mob.MobSDK
import com.shentu.paper.app.Constant
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.base.BaseBindingActivity
import com.shentu.paper.app.config.Config
import com.shentu.paper.databinding.ActivityMainBinding
import com.shentu.paper.mvp.ui.activity.BrowserActivity
import com.shentu.paper.mvp.ui.adapter.MainPagerAdapter
import com.shentu.paper.mvp.ui.home.TabHomeFragment
import com.shentu.paper.viewmodels.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_agreement.view.*
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>(),
    NavigationBarView.OnItemSelectedListener {

    private var lastPos: Int = 0//上一个位置
    private val accountViewModel by viewModels<AccountViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Config.init(this)
        }
        if (SPUtils.getInstance().getBoolean("APP_SHOW_POLICY", true)) {
            showPolicyDialog()
        } else {
            loadData()
        }
    }

    override fun onResume() {
        super.onResume()
        switchStatusBar()
    }


    private fun loadData() {
        accountViewModel.loadAccountInfo()
        accountViewModel.liveData.observe(this, {
            if (!it.isSuccess) {
                Timber.e("load error")
                showError()
                return@observe
            }
            HkUserManager.user = it.data!!
            HkUserManager.save()
            showMainView()
            sign()
        })
    }

    private fun sign() {
        accountViewModel.signLiveData.observe(this, {
            binding.apply {
                llSign.visibility = View.VISIBLE
                tvSign.text = "每日登录：+$it"
                llSign.scaleX = 0f
                llSign.scaleY = 0f
                llSign.animate().scaleX(1f).scaleY(1f).setStartDelay(1200).start()
                lottieSign.playAnimation()
            }
            HkUserManager.updateKandou(it)
        })
    }

    private fun showMainView() {
        binding.apply {
            navigationView.setOnItemSelectedListener(this@MainActivity)
//            navigationView.setOnItemReselectedListener(this@MainFragment)
            mainPager.isUserInputEnabled = false
            mainPager.offscreenPageLimit = 2
            mainPager.adapter = MainPagerAdapter(this@MainActivity)
            lottieSign.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(p0: Animator?) {
                    llSign.visibility = View.GONE
                }
            })
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bottom_nav_hot -> {
                mainPager.setCurrentItem(0, false)
                lastPos = 0
            }
//            R.id.navigation_category -> {
//                viewPager.setCurrentItem(1, false);lastPos = 1
//            }
            R.id.bottom_nav_my -> {
                mainPager.setCurrentItem(1, false)
                lastPos = 2
            }
        }
        switchStatusBar()
        return true
    }

    private fun switchStatusBar() {
        if (mainPager.adapter == null) {
            return
        }
        when (binding.mainPager.currentItem) {
            0 -> {
                BarUtils.setStatusBarLightMode(
                    this,
                    ((mainPager.adapter as MainPagerAdapter).fragments[0] as TabHomeFragment).getIsLightMode()
                )
            }
            1 -> {
                BarUtils.setStatusBarLightMode(this, true)
            }
            else -> {
                BarUtils.setStatusBarLightMode(this, false)
            }
        }
    }

    private var lastTime: Long = 0

//    override fun onBackPressed() {
//        if (System.currentTimeMillis() - lastTime < 2000) {
//            if (viewPager.currentItem == 0 && (fragments[0] as TabHomeFragment).isScrolled()) {
//                (fragments[0] as TabHomeFragment).scrollToTop()
//            } else {
//                super.onBackPressed()
//            }
//        } else {
//            if (viewPager.currentItem == 0 && (fragments[0] as TabHomeFragment).isScrolled()) {
//                (fragments[0] as TabHomeFragment).scrollToTop()
//            } else {
//                ToastUtils.showShort("再点一次返回")
//                lastTime = System.currentTimeMillis()
//            }
//        }
//    }

    private fun showPolicyDialog() {
        val dialog = MaterialDialog(this).show {
            customView(R.layout.dialog_agreement)
            cancelable(false)
        }
        dialog.window!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.bg_dialog_change_tip
            )
        )
        val tvContent: TextView = dialog.getCustomView().tvContent
        tvContent.movementMethod = LinkMovementMethod.getInstance()
        tvContent.text = SpanUtils()
            .append("感谢您下载${resources.getString(R.string.app_name)}！我们非常重视您的个人信息和隐私保护。为了更好地保障您的个人利益，在使用我们的产品前，请您务必审慎阅读、充分理解")
            .append("《服务协议》")
            .setClickSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    BrowserActivity.open(this@MainActivity, Constant.WEB_SERVER)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@MainActivity, R.color.colorAccent)
                }
            })
            .append("《隐私政策》")
            .setClickSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    BrowserActivity.open(this@MainActivity, Constant.WEB_PRIVACY)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(this@MainActivity, R.color.colorAccent)
                }
            })
            .append("各条款，我们会按照上述政策收集、使用和共享您的个人信息。如您同意，请点击“同意”开始接受我们的服务。")
            .create()
        dialog.getCustomView().tvConfirm.setOnClickListener {
            MobSDK.submitPolicyGrantResult(true, null)
            SPUtils.getInstance().put("APP_SHOW_POLICY", false)
            dialog.dismiss()
            loadData()
        }
        dialog.getCustomView().tvCancel.setOnClickListener {
            AppManager.getAppManager().appExit()
        }
    }
}