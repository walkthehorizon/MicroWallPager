package com.shentu.wallpaper.mvp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ScreenUtils
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadSampleListener
import com.liulishuo.filedownloader.FileDownloader
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.event.LoadOriginPictureEvent
import com.shentu.wallpaper.app.event.LoadOriginResultEvent
import com.shentu.wallpaper.app.event.SwitchNavigationEvent
import com.shentu.wallpaper.app.utils.PicUtils
import com.shentu.wallpaper.di.component.DaggerPictureBrowserComponent
import com.shentu.wallpaper.di.module.PictureBrowserModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.shentu.wallpaper.mvp.presenter.PictureBrowserPresenter
import com.shentu.wallpaper.mvp.ui.adapter.PictureBrowserVpAdapter
import kotlinx.android.synthetic.main.fragment_picture_browser.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = "/picture/browser/activity")
class PictureBrowserActivity : BaseActivity<PictureBrowserPresenter>(), PictureBrowserContract.View, ViewPager.OnPageChangeListener {

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
        initSetCover()
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
        vpAdapter = PictureBrowserVpAdapter(supportFragmentManager, wallpapers)
        viewPager.adapter = vpAdapter
        viewPager.currentItem = current
        onPageSelected(current)

        ivDownload.setOnClickListener {
            FileDownloader.getImpl().create(wallpapers[viewPager.currentItem].originUrl)
                    .setPath(PathUtils.getExternalPicturesPath()).listener = object : FileDownloadSampleListener() {
                override fun completed(task: BaseDownloadTask?) {
                    showMessage("文件已存储：" + task!!.targetFilePath)
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    if (e != null) {
                        showMessage("下载失败：" + e.message)
                    }
                }
            }
        }
        mbLoadOrigin.setOnClickListener {
            mbLoadOrigin.isEnabled = false//在结果回调前禁用二次点击
            EventBus.getDefault().post(LoadOriginPictureEvent(wallpapers[viewPager.currentItem].id))
        }
        ivDownload.setOnClickListener {
            ivDownload.visibility = View.GONE
            mPresenter?.downloadPicture(wallpapers[viewPager.currentItem].originUrl!!)
        }
    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
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
    }

    override fun showNavigation() {
        rl_head.visibility = View.VISIBLE
        rl_bottom.visibility = View.VISIBLE
    }

    override fun hideNavigation() {
        rl_head.visibility = View.GONE
        rl_bottom.visibility = View.GONE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun switchNavigation(event: SwitchNavigationEvent) {
        if (rl_head.visibility == View.VISIBLE) {
            hideNavigation()
        } else {
            showNavigation()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadOriginResult(event: LoadOriginResultEvent) {
        for (wallpaper in wallpapers) {
            if (wallpaper.id == event.id) {
                if (event.result) {
                    mbLoadOrigin.visibility = View.GONE
                    wallpaper.isOriginExist = true
                } else {
                    mbLoadOrigin.isEnabled = true
                }
            }
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