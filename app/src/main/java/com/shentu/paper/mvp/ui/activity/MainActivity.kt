package com.shentu.paper.mvp.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blankj.utilcode.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.horizon.netbus.NetBus
import com.jess.arms.base.BaseActivity
import com.jess.arms.integration.AppManager
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.Preconditions.checkNotNull
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.mob.MobSDK
import com.mob.OperationCallback
import com.shentu.paper.R
import com.shentu.paper.app.Constant
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.config.Config
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.api.service.UserService
import com.shentu.paper.model.response.BaseResponse
import com.shentu.paper.mvp.contract.MainContract
import com.shentu.paper.mvp.presenter.MainPresenter
import com.shentu.paper.mvp.ui.adapter.MainPagerAdapter
import com.shentu.paper.mvp.ui.home.TabHomeFragment
import com.shentu.paper.mvp.ui.my.TabMyFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_agreement.view.*
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<MainPresenter>(), MainContract.View,
    ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener {

    private lateinit var mainPagerAdapter: MainPagerAdapter
    private var lastPos: Int = 0//上一个位置
    private val fragments: List<Fragment> =
        listOf(TabHomeFragment.newInstance(), TabMyFragment.newInstance())
    private var loadService: LoadService<Any>? = null

    @Inject
    lateinit var repositoryManager: IRepositoryManager

    @Inject
    lateinit var rxErrorHandler: RxErrorHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        Config.init(this)
        loadService = LoadSir.getDefault().register(this) { initData(null) }
//        if(HkUserManager.isLogin){
//            showMainView()
//            GlobalScope.launch {
//                showContent()
//            }
//        }else{
        mPresenter?.getAccountInfo()
//        }
    }

    override fun showMainView() {
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager, fragments)
        viewPager.offscreenPageLimit = mainPagerAdapter.count
        viewPager.addOnPageChangeListener(this)
        viewPager.adapter = mainPagerAdapter
        navigationView.setOnNavigationItemSelectedListener(this)
        navigationView.setOnNavigationItemReselectedListener(this)
        lottieSign.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(p0: Animator?) {
                llSign.visibility = View.GONE
            }
        })
        sign()
//        BrowserActivity.open(this,"https://webinterdev.innodealing.com/international-auth-service/login/mobile")
//        BrowserActivity.open(this,"https://mp.weixin.qq.com/s/9a5ZC1jFeYvs0_awAyYvEQ")
        if (SPUtils.getInstance().getBoolean("APP_SHOW_POLICY", true)) {
            showPolicyDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        switchStatusBar()
    }

    override fun showMessage(message: String) {
        checkNotNull(message)
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }

    override fun showContent() {
        loadService?.showSuccess()
    }

    override fun showError() {
        loadService?.showCallback(ErrorCallback::class.java)
    }

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
            MobSDK.submitPolicyGrantResult(true, object : OperationCallback<Void>() {
                override fun onFailure(p0: Throwable?) {
                    Timber.e(p0?.cause)
                }

                override fun onComplete(p0: Void?) {
                    Timber.e("mob 授权成功")
                }
            })
            SPUtils.getInstance().put("APP_SHOW_POLICY", false)
            dialog.dismiss()
        }
        dialog.getCustomView().tvCancel.setOnClickListener {
            AppManager.getAppManager().appExit()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        if (position == lastPos) {
            return
        }
        if (position < 0 || position >= viewPager.adapter!!.count) {
            return
        }
        lastPos = position
        when (position) {
            0 -> navigationView.selectedItemId = R.id.navigation_hot
//            1 -> binding.navigationView.selectedItemId = R.id.navigation_category
            2 -> navigationView.selectedItemId = R.id.navigation_my
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_hot -> {
                viewPager.setCurrentItem(0, false);lastPos = 0
            }
//            R.id.navigation_category -> {
//                viewPager.setCurrentItem(1, false);lastPos = 1
//            }
            R.id.navigation_my -> {
                viewPager.setCurrentItem(2, false);lastPos = 2
            }
        }
        switchStatusBar()
        return true
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        Timber.e("reselected: %s", item.title)
    }

    private var lastTime: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() - lastTime < 2000) {
            if (viewPager.currentItem == 0 && (fragments[0] as TabHomeFragment).isScrolled()) {
                (fragments[0] as TabHomeFragment).scrollToTop()
            } else {
                super.onBackPressed()
            }
        } else {
            if (viewPager.currentItem == 0 && (fragments[0] as TabHomeFragment).isScrolled()) {
                (fragments[0] as TabHomeFragment).scrollToTop()
            } else {
                ToastUtils.showShort("再点一次返回")
                lastTime = System.currentTimeMillis()
            }
        }
    }

    private fun switchStatusBar() {
        when (viewPager.currentItem) {
            0 -> {
                BarUtils.setStatusBarLightMode(
                    this,
                    (fragments[0] as TabHomeFragment).getIsLightMode()
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

    override fun onDestroy() {
        super.onDestroy()
        NetBus.getInstance().unRegister(this)
    }

    /**
     * 签到
     * */
    private fun sign() {
        if (!HkUserManager.isLogin) {
            return
        }
        val lastSignMillis = SPUtils.getInstance().getLong(Constant.LAST_SIGN_TIME, 0)
        if (TimeUtils.isToday(lastSignMillis)) {
            Timber.i("今日已签到,签到时间：%s", TimeUtils.millis2String(lastSignMillis))
            return
        }
        SPUtils.getInstance().put(Constant.LAST_SIGN_TIME, System.currentTimeMillis())
        repositoryManager
            .obtainRetrofitService(UserService::class.java)
            .sign()
            .compose(RxUtils.applyClearSchedulers(this))
            .subscribe(object : ErrorHandleSubscriber<BaseResponse<Int>>(
                rxErrorHandler
            ) {
                @SuppressLint("SetTextI18n")
                override fun onNext(t: BaseResponse<Int>) {
                    if (!t.isSuccess) {
                        return
                    }
                    llSign.visibility = View.VISIBLE
                    tvSign.text = "每日登录：+" + t.data
                    llSign.scaleX = 0f
                    llSign.scaleY = 0f
                    llSign.animate().scaleX(1f).scaleY(1f).setStartDelay(1200).start()
                    lottieSign.playAnimation()
                    t.data?.let { HkUserManager.updateKandou(it) }
                }
            })
    }
}
