package com.shentu.wallpaper.mvp.ui.browser

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.transition.Fade
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityOptionsCompat
import androidx.viewpager.widget.ViewPager
import cn.sharesdk.onekeyshare.OnekeyShare
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.piasy.biv.BigImageViewer
import com.google.android.material.button.MaterialButton
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.mob.moblink.MobLink
import com.mob.moblink.Scene
import com.mob.moblink.SceneRestorable
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.di.component.DaggerPictureBrowserComponent
import com.shentu.wallpaper.di.module.PictureBrowserModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.shentu.wallpaper.mvp.presenter.PictureBrowserPresenter
import com.shentu.wallpaper.mvp.ui.activity.BrowserActivity
import com.shentu.wallpaper.mvp.ui.activity.SubjectDetailActivity
import com.shentu.wallpaper.mvp.ui.adapter.PictureBrowserVpAdapter
import com.shentu.wallpaper.mvp.ui.fragment.PictureFragment
import com.shentu.wallpaper.mvp.ui.home.TabHomeFragment
import com.shentu.wallpaper.mvp.ui.login.LoginActivity
import com.shentu.wallpaper.mvp.ui.my.DonateDialog
import kotlinx.android.synthetic.main.fragment_picture_browser.*
import timber.log.Timber

@Route(path = "/picture/browser/activity")
class PictureBrowserActivity : BaseActivity<PictureBrowserPresenter>(), PictureBrowserContract.View
        , ViewPager.OnPageChangeListener, PictureFragment.Callback, SceneRestorable {

    @Autowired
    @JvmField
    var subjectId: Int = -1
    @Autowired
    @JvmField
    var current: Int = 0
    @Autowired
    @JvmField
    var categoryId: Int = -1
    //from web
    private var paperId:Int=-1

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
            paperId !=-1 ->mPresenter?.getPaperDetail(paperId)
            else -> throw IllegalArgumentException("参数异常")
        }
        ivShare.setOnClickListener {
            mPresenter?.getShareData(wallpapers[viewPager.currentItem])
        }
        initSetCover()
        Timber.e("presenter:"+(mPresenter==null))
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

    private fun showDownloadDialog(paper: Wallpaper) {
        val type = SPUtils.getInstance().getInt(Constant.DOWNLOAD_TYPE, 0)
        if (type != 0) {
//            ivDownload.visibility = View.GONE
            mPresenter?.buyPaper(viewPager.currentItem, paper
                    , if (type == 1) SaveType.NORMAL else SaveType.ORIGIN)
        } else {
            var checked = false
            var result = SaveType.NORMAL.value
            MaterialDialog(this)
                    .title(text = "下载")
                    .listItemsSingleChoice(items = listOf("默认（${paper.normalPrice}看豆）"
                            , "原图（${paper.originPrice}看豆）")
                            , initialSelection = 0) { _, index, _ ->
                        val saveType = if (index == 0) SaveType.NORMAL else SaveType.ORIGIN
                        result = saveType.value
                        mPresenter?.buyPaper(viewPager.currentItem, wallpapers[viewPager.currentItem]
                                , if (index == 0) SaveType.NORMAL else SaveType.ORIGIN)
                    }
                    .positiveButton(text = "确认") {
                        if (checked) {
                            SPUtils.getInstance().put(Constant.DOWNLOAD_TYPE, result)
                        }
                    }
                    .negativeButton(text = "取消")
                    .checkBoxPrompt(text = "不再显示") {
                        checked = it
                    }.show()
        }
    }

    private fun initSetCover() {
        Timber.e("admin:$categoryId")
        if (!HkUserManager.getInstance().isAdmin || categoryId == -1) {
            return
        }
        tvSetCover.visibility = View.VISIBLE
        tvSetCover.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "分类")
                message(text = "确定设为当前分类封面？")
                positiveButton(text = "确定") {
                    val index = this@PictureBrowserActivity.viewPager.currentItem
                    mPresenter?.updateCategoryCover(categoryId, wallpapers[index].url)
                }
                negativeButton(text = "取消")
            }
        }
    }

    private fun initViewPager() {
        check(wallpapers.isNotEmpty()) { "wallpapers size can not < 1" }

        mbLoadOrigin.setOnClickListener {
            //            mbLoadOrigin.isEnabled = false//在结果回调前禁用二次点击
            vpAdapter.getFragment(viewPager.currentItem).loadOriginPicture(Behavior.ONLY_LOAD)
        }
        ivDownload.setOnClickListener {
            if (!HkUserManager.getInstance().isLogin) {
                launchActivity(Intent(this@PictureBrowserActivity, LoginActivity::class.java))
                return@setOnClickListener
            }
            showDownloadDialog(wallpapers[viewPager.currentItem])
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
//        mbLoadOrigin.visibility = if (wallpapers[position].isOriginExist) View.GONE else View.VISIBLE
//        ivDownload.visibility = if (FileUtils.isFileExists(PicUtils.getInstance().getDownloadPicturePath(
//                        wallpapers[position].originUrl))) View.GONE else View.VISIBLE
        tvCollectNum.text = wallpapers[position].collectNum.toString()
        ivCollect.isClickable = !wallpapers[position].collected
        ivCollect.progress = if (wallpapers[position].collected) 1f else 0f
    }

    override fun showDonateDialog() {
        val dialog = MaterialDialog(this).show {
            customView(R.layout.activity_donate)
            cornerRadius(12f)
        }
        dialog.getCustomView().findViewById<TextView>(R.id.tvQuestion).setOnClickListener {
            BrowserActivity.open(this, "https://www.baidu.com/")
        }
        dialog.getCustomView().findViewById<MaterialButton>(R.id.mbDonate).setOnClickListener {
            HkUtils.contactKefu()
        }
    }

    override fun showNavigation() {
        rl_head.visibility = View.VISIBLE
        rl_bottom.visibility = View.VISIBLE
        ivShare.visibility = View.VISIBLE
    }

    override fun hideNavigation() {
        rl_head.visibility = View.GONE
        rl_bottom.visibility = View.GONE
        ivShare.visibility = View.GONE
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
//        if (pos == viewPager.currentItem) {
//            mbLoadOrigin.visibility = View.GONE
//        }
    }

    /**
     * 先于initData执行
     * */
    override fun onReturnSceneData(scene: Scene) {
        paperId = scene.params["id"] as Int
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        MobLink.updateNewIntent(getIntent(), this)
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