package com.shentu.wallpaper.mvp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.app.utils.PicUtils
import com.shentu.wallpaper.di.component.DaggerPictureBrowserComponent
import com.shentu.wallpaper.di.module.PictureBrowserModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.shentu.wallpaper.mvp.presenter.PictureBrowserPresenter
import com.shentu.wallpaper.mvp.ui.adapter.PictureBrowserVpAdapter
import com.shentu.wallpaper.mvp.ui.fragment.PictureFragment
import kotlinx.android.synthetic.main.fragment_picture_browser.*

@Route(path = "/picture/browser/activity")
class PictureBrowserActivity : BaseActivity<PictureBrowserPresenter>(), PictureBrowserContract.View
        , ViewPager.OnPageChangeListener, PictureFragment.Callback {

    @Autowired
    @JvmField
    var subjectId: Int = -1
    //1、wallpaper2、subject
    @Autowired
    @JvmField
    var type: Int = -1
    @Autowired
    @JvmField
    var wallpapers: ArrayList<Wallpaper> = ArrayList()
    @Autowired
    @JvmField
    var current: Int = 0
    @Autowired
    @JvmField
    var categoryId: Int = -1
    private var popupMenu: PopupMenu? = null

    private lateinit var vpAdapter: PictureBrowserVpAdapter

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerPictureBrowserComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .pictureBrowserModule(PictureBrowserModule(this))
                .build()
                .inject(this)
        ScreenUtils.setFullScreen(this)
        window.enterTransition = Fade()
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_picture_browser
    }

    override fun initData(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        if (type == -1) {
            throw IllegalStateException("picture browser type can not null")
        }
        if (type == 1) {
            initViewPager()
        }
        if (type == 2) {
            mPresenter?.getPictures(subjectId)
        }
        ivMore.setOnClickListener {
            showMenu()
        }
        ivCollect.setOnClickListener {
            mPresenter?.addCollect(wallpapers[viewPager.currentItem].id)
        }
        initSetCover()
    }

    private fun showMenu() {
        if (popupMenu == null) {
            popupMenu = PopupMenu(this, ivMore, Gravity.BOTTOM)
            popupMenu!!.menuInflater.inflate(R.menu.menu_picture_detail, popupMenu!!.menu)
            popupMenu!!.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.itSetPaper -> mPresenter?.downloadPicture(wallpapers[viewPager
                            .currentItem].originUrl, 1)
                    R.id.itSubject -> SubjectDetailActivity.open(wallpapers[viewPager.currentItem].subjectId)
                }
                true
            }
        }
        popupMenu!!.show()
    }

    override fun showCollect() {
        ivCollect.isSelected = !ivCollect.isSelected
        val wallpaper = wallpapers[viewPager.currentItem]
        wallpaper.collectNum = if (ivCollect.isSelected) wallpaper.collectNum + 1 else wallpaper.collectNum - 1
        tvCollectNum.text = wallpaper.collectNum.toString()
    }

    override fun setWallpaper(path: String) {
        HkUtils.setWallpaper(this, path)
    }

    private fun initSetCover() {
        if (!HkUserManager.getInstance().isAdmin || categoryId == -1) {
            return
        }
        tvSetCover.visibility = View.VISIBLE
        tvSetCover.setOnClickListener {
            MaterialDialog.Builder(this)
                    .title("分类")
                    .content("确定设为当前分类封面？")
                    .positiveText("确定")
                    .negativeText("取消")
                    .onPositive { _, _ ->
                        mPresenter?.updateCategoryCover(categoryId
                                , wallpapers[viewPager.currentItem].url)
                    }
                    .onNegative { dialog, _ -> dialog.dismiss() }
                    .show()
        }
    }

    private fun initViewPager() {
        if (wallpapers.size == 0) {
            throw IllegalStateException("wallpapers size can not < 1")
        }
        viewPager.addOnPageChangeListener(this)
        viewPager.offscreenPageLimit = 4
        vpAdapter = PictureBrowserVpAdapter(supportFragmentManager, wallpapers, this)
        viewPager.adapter = vpAdapter
        viewPager.currentItem = current
        onPageSelected(current)
        mbLoadOrigin.setOnClickListener {
            mbLoadOrigin.isEnabled = false//在结果回调前禁用二次点击
            vpAdapter.getFragment(viewPager.currentItem).loadOriginPicture()
        }
        ivDownload.setOnClickListener {
            ivDownload.visibility = View.GONE
            mPresenter?.downloadPicture(wallpapers[viewPager.currentItem].url)
        }
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        ScreenUtils.setNonFullScreen(this)
        finishAfterTransition()
    }

    override fun onBackPressed() {
        ScreenUtils.setNonFullScreen(this)
        super.onBackPressed()
    }

    override fun showPictures(pictures: MutableList<Wallpaper>) {
        wallpapers = pictures as ArrayList<Wallpaper>
        initViewPager()
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onPageSelected(position: Int) {
        tvOrder.text = "${position + 1}/${vpAdapter.count}"
        mbLoadOrigin.visibility = if (wallpapers[position].isOriginExist) View.GONE else View.VISIBLE
        ivDownload.visibility = if (FileUtils.isFileExists(PicUtils.getInstance().getDownloadPicturePath(
                        wallpapers[position].originUrl))) View.GONE else View.VISIBLE
        tvCollectNum.text = wallpapers[position].collectNum.toString()
    }

    override fun showNavigation() {
        rl_head.visibility = View.VISIBLE
        rl_bottom.visibility = View.VISIBLE
    }

    override fun hideNavigation() {
        rl_head.visibility = View.GONE
        rl_bottom.visibility = View.GONE
    }

    override fun switchNavigation() {
        if (rl_head.visibility == View.VISIBLE) {
            hideNavigation()
        } else {
            showNavigation()
        }
    }

    override fun onLoadOrigin(pos: Int, result: Boolean) {
        wallpapers[pos].isOriginExist = result
        if (pos == viewPager.currentItem) {
            mbLoadOrigin.visibility = View.GONE
        }
    }

    companion object {
        fun open(context: Context, subjectId: Int) {
            ARouter.getInstance()
                    .build("/picture/browser/activity")
                    .withInt("type", 2)
                    .withInt("subjectId", subjectId)
                    .navigation(context)
        }

        fun open(context: Context, wallpapers: List<Wallpaper>, current: Int, compat: ActivityOptionsCompat? = null) {
            ARouter.getInstance()
                    .build("/picture/browser/activity")
                    .withInt("type", 1)
                    .withSerializable("wallpapers", wallpapers as ArrayList)
                    .withInt("current", current)
                    .withOptionsCompat(compat)
                    .navigation(context)
        }

        fun open(context: Context, categoryId: Int, wallpapers: List<Wallpaper>, current: Int, compat: ActivityOptionsCompat? = null) {
            ARouter.getInstance()
                    .build("/picture/browser/activity")
                    .withInt("type", 1)
                    .withSerializable("wallpapers", wallpapers as ArrayList)
                    .withInt("categoryId", categoryId)
                    .withInt("current", current)
                    .withOptionsCompat(compat)
                    .navigation(context)
        }
    }
}