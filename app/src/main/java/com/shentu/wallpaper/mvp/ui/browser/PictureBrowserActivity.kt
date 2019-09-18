package com.shentu.wallpaper.mvp.ui.browser

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.transition.Fade
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager.widget.ViewPager
import cn.sharesdk.onekeyshare.OnekeyShare
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.piasy.biv.BigImageViewer
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.di.component.DaggerPictureBrowserComponent
import com.shentu.wallpaper.di.module.PictureBrowserModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.shentu.wallpaper.mvp.presenter.PictureBrowserPresenter
import com.shentu.wallpaper.mvp.ui.activity.SubjectDetailActivity
import com.shentu.wallpaper.mvp.ui.adapter.PictureBrowserVpAdapter
import com.shentu.wallpaper.mvp.ui.fragment.PictureFragment
import com.shentu.wallpaper.mvp.ui.home.TabHomeFragment
import com.shentu.wallpaper.mvp.ui.login.LoginActivity
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
    var current: Int = 0
    @Autowired
    @JvmField
    var categoryId: Int = -1
    private var popupMenu: PopupMenu? = null

    private var wallpapers: MutableList<Wallpaper> = arrayListOf()
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
        when {
            callback != null -> {
                if (callback is TabHomeFragment) {
                    current -= 1
                    wallpapers.addAll(callback!!.getWallpaperList().subList(1,
                            callback!!.getWallpaperList().size - 1))
                } else {
                    wallpapers.addAll(callback!!.getWallpaperList())
                }
                initViewPager()
            }
            subjectId != -1 -> mPresenter?.getPictures(subjectId)
            else -> throw IllegalStateException("数据获取异常！")
        }
        ivShare.setOnClickListener {
            mPresenter?.getShareData(wallpapers[viewPager.currentItem])

        }
        initSetCover()
    }

    /**
     * 展示分享九宫格
     * @param paper 带有详细数据的Wallpaper
     * */
    override fun showShare(paper: Wallpaper) {
        val oks = OnekeyShare()
        oks.disableSSOWhenAuthorize()
        oks.text = paper.description
        oks.setTitle(paper.title)
        oks.setImageUrl(paper.url)
        oks.setTitleUrl(Constant.BASE_WALLPAPER_SHARE_URL + paper.id)
        oks.setUrl(Constant.BASE_WALLPAPER_SHARE_URL + paper.id)
        oks.setDialogMode(false)
        oks.setSite(resources.getString(R.string.app_name))
        oks.setSiteUrl(Constant.MICRO_BASE_URL)
        //自定义打开图片分享
        val customLogo = BitmapFactory.decodeResource(resources, R.drawable.ic_share_picture)
        val label = "分享图片"
        val listener = View.OnClickListener {
            ARouter.getInstance().build("/activity/picture/share")
                    .withSerializable("paper", paper)
                    .navigation(this)
        }
        oks.setCustomerLogo(customLogo, label, listener)
        oks.show(this)
    }

    private fun showMenu() {
        if (popupMenu == null) {
            popupMenu = PopupMenu(this, ivMore, Gravity.BOTTOM)
            popupMenu!!.menuInflater.inflate(R.menu.menu_picture_detail, popupMenu!!.menu)
            popupMenu!!.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.itSetPaper -> vpAdapter.getFragment(viewPager.currentItem).loadOriginPicture(Behavior.SET_WALLPAPER)
                    R.id.itSubject -> SubjectDetailActivity.open(wallpapers[viewPager.currentItem].subjectId, this)
                }
                true
            }
        }
        popupMenu!!.show()
    }

    override fun setWallpaper(path: String) {
        HkUtils.setWallpaper(this, path)
    }

    override fun savePicture(currentItem: Int, type: SaveType) {
        vpAdapter.getFragment(currentItem).savePicture(type)
    }

    private fun showDownloadDialog() {
        var pea = SPUtils.getInstance().getInt(Constant.DEFAULT_DOWNLOAD_RESUME, -1)
        if (pea != -1) {
//            ivDownload.visibility = View.GONE
            mPresenter?.buyPaper(viewPager.currentItem, wallpapers[viewPager.currentItem], pea)
        } else {
            pea = 1
            MaterialDialog(this)
                    .title(text = "下载")
                    .listItemsSingleChoice(items = listOf("默认（1看豆）", "原图（3看豆）")
                            , initialSelection = 0) { _, index, _ ->
                        pea = if (index == 0) 1 else 3
//                        ivDownload.visibility = View.GONE
                        mPresenter?.buyPaper(viewPager.currentItem, wallpapers[viewPager.currentItem], pea)
                    }
                    .positiveButton(text = "确认")
                    .negativeButton(text = "取消")
                    .checkBoxPrompt(text = "不再显示") {
                        if (it) {
                            SPUtils.getInstance().put(Constant.DEFAULT_DOWNLOAD_RESUME, pea)
                        }
                    }.show()
        }
    }

    private fun initSetCover() {
        if (!HkUserManager.getInstance().isAdmin || categoryId == -1) {
            return
        }
        tvSetCover.visibility = View.VISIBLE
        tvSetCover.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "分类")
                message(text = "确定设为当前分类封面？")
                positiveButton(text = "确定") {
                    mPresenter?.updateCategoryCover(categoryId, wallpapers[viewPager.currentItem].url)
                }
                negativeButton(text = "取消")
            }
        }
    }

    private fun initViewPager() {
        check(wallpapers.isNotEmpty()) { "wallpapers size can not < 1" }

        mbLoadOrigin.setOnClickListener {
            mbLoadOrigin.isEnabled = false//在结果回调前禁用二次点击
            vpAdapter.getFragment(viewPager.currentItem).loadOriginPicture(Behavior.ONLY_LOAD)
        }
        ivDownload.setOnClickListener {
            if (!HkUserManager.getInstance().isLogin) {
                launchActivity(Intent(this@PictureBrowserActivity, LoginActivity::class.java))
                return@setOnClickListener
            }
            if (HkUserManager.getInstance().user.pea < 1) {
                HkUtils.instance.showChargeDialog(this@PictureBrowserActivity)
                return@setOnClickListener
            }
            showDownloadDialog()
        }
        ivMore.setOnClickListener {
            showMenu()
        }
        ivCollect.setOnClickListener {
            if (!HkUserManager.getInstance().isLogin) {
                launchActivity(Intent(this, LoginActivity::class.java))
                return@setOnClickListener
            }
            ivCollect.isClickable = false
            mPresenter?.addCollect(wallpapers[viewPager.currentItem].id, viewPager.currentItem)
        }

        viewPager.addOnPageChangeListener(this)
        viewPager.offscreenPageLimit = 4
        vpAdapter = PictureBrowserVpAdapter(supportFragmentManager, wallpapers, this)
        viewPager.adapter = vpAdapter
        viewPager.currentItem = current
        onPageSelected(current)
    }

    override fun resetCollect() {
        ivCollect.isClickable = true
    }

    override fun showCollectAnim(position: Int) {
        val wallpaper = wallpapers[position]
        wallpaper.collected = true
        wallpaper.collectNum = wallpaper.collectNum + 1
        if (position == viewPager.currentItem) {
            tvCollectNum.text = wallpaper.collectNum.toString()
            ivCollect.isClickable = false
            ivCollect.playAnimation()
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
        if (ivCollect.isAnimating) {
            ivCollect.cancelAnimation()
        }
        tvOrder.text = "${position + 1}/${vpAdapter.count}"
        mbLoadOrigin.visibility = if (wallpapers[position].isOriginExist) View.GONE else View.VISIBLE
//        ivDownload.visibility = if (FileUtils.isFileExists(PicUtils.getInstance().getDownloadPicturePath(
//                        wallpapers[position].originUrl))) View.GONE else View.VISIBLE
        tvCollectNum.text = wallpapers[position].collectNum.toString()
        ivCollect.isClickable = !wallpapers[position].collected
        ivCollect.progress = if (wallpapers[position].collected) 1f else 0f
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

    override fun onDestroy() {
        super.onDestroy()
        BigImageViewer.imageLoader().cancelAll()
        callback = null
    }

    companion object {
        private var callback: Callback? = null
        fun open(current: Int = 0, callback: Callback? = null, compat: ActivityOptionsCompat? = null,
                 categoryId: Int = -1, subjectId: Int = -1, context: Context) {
            Companion.callback = callback
            ARouter.getInstance()
                    .build("/picture/browser/activity")
                    .withInt("categoryId", categoryId)
                    .withInt("current", current)
                    .withInt("subjectId", subjectId)
                    .withOptionsCompat(compat)
                    .navigation(context)
        }
    }


    interface Callback {
        fun getWallpaperList(): List<Wallpaper>
    }
}