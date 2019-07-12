package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.horizon.netbus.NetBus
import com.horizon.netbus.NetType
import com.horizon.netbus.NetWork
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.Preconditions.checkNotNull
import com.shentu.wallpaper.R
import com.shentu.wallpaper.di.component.DaggerMainComponent
import com.shentu.wallpaper.di.module.MainModule
import com.shentu.wallpaper.mvp.contract.MainContract
import com.shentu.wallpaper.mvp.presenter.MainPresenter
import com.shentu.wallpaper.mvp.ui.adapter.MainPagerAdapter
import com.shentu.wallpaper.mvp.ui.fragment.TabCategoryFragment
import com.shentu.wallpaper.mvp.ui.fragment.TabMyFragment
import com.shentu.wallpaper.mvp.ui.home.TabHomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : BaseActivity<MainPresenter>(), MainContract.View, ViewPager.OnPageChangeListener
        , BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {


    private var mainPagerAdapter: MainPagerAdapter? = null
    private var lastPos: Int = 0//上一个位置
    private val fragments: List<Fragment> = listOf(TabHomeFragment.newInstance()
            , TabCategoryFragment.newInstance(), TabMyFragment.newInstance())

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
        BarUtils.setStatusBarAlpha(this)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager, fragments)
        viewPager!!.offscreenPageLimit = mainPagerAdapter!!.count
        viewPager!!.addOnPageChangeListener(this)
        viewPager!!.adapter = mainPagerAdapter
        //        HkUtils.disableShiftMode(navigationView);
        navigationView!!.setOnNavigationItemSelectedListener(this)
        navigationView!!.setOnNavigationItemReselectedListener(this)
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
            0 -> navigationView!!.selectedItemId = R.id.navigation_hot
            1 -> navigationView!!.selectedItemId = R.id.navigation_category
            2 -> navigationView!!.selectedItemId = R.id.navigation_my
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_hot -> {
                viewPager.setCurrentItem(0, false);lastPos = 0
                BarUtils.setStatusBarLightMode(this
                        , (fragments[0] as TabHomeFragment).getIsLightMode())
            }
            R.id.navigation_category -> {
                viewPager.setCurrentItem(1, false);lastPos = 1
                BarUtils.setStatusBarLightMode(this, true)
            }
            R.id.navigation_my -> {
                viewPager.setCurrentItem(2, false);lastPos = 2
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        NetBus.getInstance().unRegister(this)
        mainPagerAdapter = null
    }

    @NetWork
    fun onNetChange(type: NetType) {
        Timber.e("netType...")
        when (type) {
            NetType.NONE -> SnackbarUtils.with(window.decorView.findViewById(android.R.id.content))
                    .setMessage("网络异常！")
                    .showError()
            else -> return
        }
    }

}
