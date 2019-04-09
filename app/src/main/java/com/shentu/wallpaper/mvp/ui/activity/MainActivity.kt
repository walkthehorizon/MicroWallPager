package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.SparseIntArray
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseActivity<MainPresenter>(), MainContract.View, ViewPager.OnPageChangeListener
        , BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    @Inject
    lateinit var itemIds: SparseIntArray

    private var mainPagerAdapter: MainPagerAdapter? = null
    private var lastPos: Int = 0//上一个位置

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager)
        viewPager!!.offscreenPageLimit = mainPagerAdapter!!.count
        viewPager!!.addOnPageChangeListener(this)
        viewPager!!.adapter = mainPagerAdapter
        //        HkUtils.disableShiftMode(navigationView);
        navigationView!!.setOnNavigationItemSelectedListener(this)
        navigationView!!.setOnNavigationItemReselectedListener(this)
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
        if (position < 0 || position >= itemIds!!.size()) {
            return
        }
        lastPos = position
        navigationView!!.selectedItemId = itemIds!!.get(position)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val pos = itemIds!!.indexOfValue(item.itemId)
        if (pos == -1 || pos == lastPos) {
            return false
        }
        viewPager!!.currentItem = pos
        lastPos = pos
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
