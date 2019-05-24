package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.Preconditions.checkNotNull
import com.pgyersdk.feedback.PgyerFeedbackManager
import com.shentu.wallpaper.di.component.DaggerMainComponent
import com.shentu.wallpaper.di.module.MainModule
import com.shentu.wallpaper.mvp.contract.MainContract
import com.shentu.wallpaper.mvp.presenter.MainPresenter
import com.shentu.wallpaper.mvp.ui.adapter.MainPagerAdapter
import com.shentu.wallpaper.mvp.ui.fragment.CategoryFragment
import com.shentu.wallpaper.mvp.ui.fragment.MyFragment
import com.shentu.wallpaper.mvp.ui.home.TabHomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : BaseActivity<MainPresenter>(), MainContract.View, ViewPager.OnPageChangeListener
        , BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {


    private var mainPagerAdapter: MainPagerAdapter? = null
    private var lastPos: Int = 0//上一个位置
    private val fragments: List<Fragment> = listOf(TabHomeFragment.newInstance()
            , CategoryFragment.newInstance(), MyFragment.newInstance())

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
        return com.shentu.wallpaper.R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager, fragments)
        viewPager!!.offscreenPageLimit = mainPagerAdapter!!.count
        viewPager!!.addOnPageChangeListener(this)
        viewPager!!.adapter = mainPagerAdapter
        //        HkUtils.disableShiftMode(navigationView);
        navigationView!!.setOnNavigationItemSelectedListener(this)
        navigationView!!.setOnNavigationItemReselectedListener(this)

        PgyerFeedbackManager.PgyerFeedbackBuilder().builder().register()
    }

    override fun showMessage(message: String) {
        checkNotNull(message)
        ArmsUtils.snackbarText(message)
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
            0 -> navigationView!!.selectedItemId = com.shentu.wallpaper.R.id.navigation_hot
            1 -> navigationView!!.selectedItemId = com.shentu.wallpaper.R.id.navigation_category
            2 -> navigationView!!.selectedItemId = com.shentu.wallpaper.R.id.navigation_my
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.shentu.wallpaper.R.id.navigation_hot -> {
                viewPager.setCurrentItem(0, false);lastPos = 0
                BarUtils.setStatusBarLightMode(this
                        , (fragments[0] as TabHomeFragment).getIsLightMode())
            }
            com.shentu.wallpaper.R.id.navigation_category -> {
                viewPager.setCurrentItem(1, false);lastPos = 1
                BarUtils.setStatusBarLightMode(this, true)
            }
            com.shentu.wallpaper.R.id.navigation_my -> {
                viewPager.setCurrentItem(2, false);lastPos = 2
            }
        }
        return true
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        Timber.e("reselected: %s", item.title)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPagerAdapter = null
    }
}
