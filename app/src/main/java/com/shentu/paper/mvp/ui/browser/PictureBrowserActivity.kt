//package com.shentu.paper.mvp.ui.browser
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import android.text.InputType
//import android.transition.Fade
//import android.view.Gravity
//import android.view.View
//import android.widget.TextView
//import androidx.appcompat.widget.PopupMenu
//import androidx.core.app.ActivityOptionsCompat
//import androidx.viewpager.widget.ViewPager
//import cn.sharesdk.onekeyshare.OnekeyShare
//import com.afollestad.materialdialogs.MaterialDialog
//import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
//import com.afollestad.materialdialogs.customview.customView
//import com.afollestad.materialdialogs.customview.getCustomView
//import com.afollestad.materialdialogs.input.getInputField
//import com.afollestad.materialdialogs.input.input
//import com.afollestad.materialdialogs.list.listItemsSingleChoice
//import com.alibaba.android.arouter.facade.annotation.Autowired
//import com.alibaba.android.arouter.facade.annotation.Route
//import com.alibaba.android.arouter.launcher.ARouter
//import com.blankj.utilcode.util.SPUtils
//import com.blankj.utilcode.util.ScreenUtils
//import com.blankj.utilcode.util.ToastUtils
//import com.github.piasy.biv.BigImageViewer
//import com.google.android.material.button.MaterialButton
//import com.micro.base.BaseActivity
//import com.micro.utils.ArmsUtils
//import com.mob.moblink.MobLink
//import com.mob.moblink.Scene
//import com.mob.moblink.SceneRestorable
//import com.shentu.paper.R
//import com.shentu.paper.app.Constant
//import com.shentu.paper.app.HkUserManager
//import com.shentu.paper.app.event.LikeEvent
//import com.shentu.paper.app.utils.HkUtils
//import com.shentu.paper.model.entity.Wallpaper
//import com.shentu.paper.mvp.contract.PictureBrowserContract
//import com.shentu.paper.mvp.presenter.PictureBrowserPresenter
//import com.shentu.paper.mvp.ui.activity.SubjectDetailActivity
//import com.shentu.paper.mvp.ui.adapter.PictureBrowserVpAdapter
//import com.shentu.paper.mvp.ui.fragment.PictureFragment
//import com.shentu.paper.mvp.ui.login.LoginActivity
//import com.yanzhenjie.permission.AndPermission
//import com.yanzhenjie.permission.Permission
//import kotlinx.android.synthetic.main.fragment_picture_browser.*
//import org.greenrobot.eventbus.EventBus
//
///**
// * 图片浏览核心界面
// * */
//@Route(path = "/picture/browser/activity")
//class PictureBrowserActivity : BaseActivity<PictureBrowserPresenter>(), PictureBrowserContract.View
//        , PictureFragment.Callback, SceneRestorable {
//
//    @Autowired
//    @JvmField
//    var subjectId: Int = -1
//
//    @Autowired
//    @JvmField
//    var categoryId: Int = -1
//
//    //from web
//    private var paperId: Long = -1
//
//    private lateinit var popupMenu: PopupMenu
//
//    private var wallpapers: MutableList<Wallpaper> = arrayListOf()
//    private lateinit var vpAdapter: PictureBrowserVpAdapter
//    private lateinit var curPaper: Wallpaper
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(R.style.AppTheme_FullScreen)
//        window.enterTransition = Fade()
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun initView(savedInstanceState: Bundle?): Int {
//        return R.layout.fragment_picture_browser
//    }
//
//    override fun initData(savedInstanceState: Bundle?) {
//        ARouter.getInstance().inject(this)
//        when {
//            callback != null -> {
//                wallpapers = callback!!.getWallpaperList()
//                initView()
//            }
//            subjectId != -1 -> mPresenter?.getPictures(subjectId)
//            paperId != -1L -> mPresenter?.getPaperDetail(paperId)
//            else -> throw IllegalArgumentException("参数异常")
//        }
//    }
//
////    /**
////     * 展示分享九宫格
////     * @param paper 带有详细数据的Wallpaper
////     * */
////    override fun showShare(paper: Wallpaper) {
////        val oks = OnekeyShare()
////        oks.disableSSOWhenAuthorize()
////        oks.text = paper.description
////        oks.setTitle(paper.title)
////        oks.setImageUrl(paper.url)
////        oks.setTitleUrl(Constant.BASE_WALLPAPER_SHARE_URL + paper.id)
////        oks.setUrl(Constant.BASE_WALLPAPER_SHARE_URL + paper.id)
////        oks.setDialogMode(false)
////        oks.setSite(resources.getString(R.string.app_name))
////        oks.setSiteUrl(Constant.MICRO_BASE_URL)
////        //自定义打开图片分享
////        val customLogo = BitmapFactory.decodeResource(resources, R.drawable.ic_share_picture)
////        val label = "分享图片"
////        val listener = View.OnClickListener {
////            ARouter.getInstance().build("/activity/picture/share")
////                    .withSerializable("paper", paper)
////                    .navigation(this)
////        }
////        oks.setCustomerLogo(customLogo, label, listener)
////        oks.show(this)
////    }
//
////    private fun showMenu() {
////        if (!this::popupMenu.isInitialized) {
////            popupMenu = PopupMenu(this, ivMore, Gravity.BOTTOM)
////            popupMenu.menuInflater.inflate(R.menu.menu_picture_detail, popupMenu.menu)
////            if (!HkUserManager.isAdmin || categoryId == -1) {
////                popupMenu.menu.findItem(R.id.itSetBanner).isVisible = false
////                popupMenu.menu.findItem(R.id.itSetCover).isVisible = false
////            }
////            if (curPaper.subjectId == -1) {
////                popupMenu.menu.findItem(R.id.itSubject).isVisible = false
////            }
////            popupMenu.setOnMenuItemClickListener { item ->
////                when (item?.itemId) {
////                    R.id.itSetPaper -> vpAdapter.getFragment(viewPager.currentItem).loadPicture(Behavior.SET_WALLPAPER)
////                    R.id.itSubject -> SubjectDetailActivity.open(curPaper.subjectId, this)
////                    R.id.itSetCover -> MaterialDialog(this).show {
////                        title(text = "分类")
////                        message(text = "确定设为当前分类封面？")
////                        positiveButton(text = "确定") {
////                            val index = this@PictureBrowserActivity.viewPager.currentItem
////                            mPresenter?.updateCategoryCover(categoryId, wallpapers[index].url)
////                        }
////                        negativeButton(text = "取消")
////                    }
////                    R.id.itSetBanner -> MaterialDialog(this).show {
////                        input(hint = "输入banner_id", inputType = InputType.TYPE_CLASS_NUMBER)
////                        positiveButton(text = "确定") {
////                            val index = this@PictureBrowserActivity.viewPager.currentItem
////                            mPresenter?.addPaper2Banner(it.getInputField().text.toString().toInt(), wallpapers[index].id)
////                        }
////                    }
////                    R.id.itSetGarbage ->mPresenter?.setGarbage(curPaper.id)
////                }
////                true
////            }
////        }
////        popupMenu.show()
////    }
//
////    override fun setWallpaper(path: String) {
////        HkUtils.setWallpaper(this, path)
////    }
////
////    override fun savePicture(currentItem: Int, type: SaveType) {
////        AndPermission.with(this)
////                .runtime()
////                .permission(Permission.WRITE_EXTERNAL_STORAGE)
////                .onGranted {
////                    vpAdapter.getFragment(currentItem).downLoadPicture(type)
////                }
////                .onDenied {
////                    ToastUtils.showShort("未获取权限,请授予存储权限后再试")
////                }.start()
////    }
//
//    private fun showDownloadDialog(paper: Wallpaper) {
//        val type = SPUtils.getInstance().getInt(Constant.DOWNLOAD_TYPE, 0)
//        if (type != 0) {
////            ivDownload.visibility = View.GONE
//            mPresenter?.buyPaper(viewPager.currentItem, paper
//                    , if (type == 1) SaveType.NORMAL else SaveType.ORIGIN)
//            showDonateDialog()
//        } else {
//            var checked = false
//            var result = SaveType.NORMAL.value
//            MaterialDialog(this)
//                    .title(text = "下载")
//                    .listItemsSingleChoice(items = listOf("默认（${HkUserManager.user.nPrice}看豆）"
//                            , "原图（${HkUserManager.user.oPrice}看豆）")
//                            , initialSelection = 0) { _, index, _ ->
//                        val saveType = if (index == 0) SaveType.NORMAL else SaveType.ORIGIN
//                        result = saveType.value
//                        mPresenter?.buyPaper(viewPager.currentItem, curPaper
//                                , if (index == 0) SaveType.NORMAL else SaveType.ORIGIN)
//                        showDonateDialog()
//                    }
//                    .positiveButton(text = "确认") {
//                        if (checked) {
//                            SPUtils.getInstance().put(Constant.DOWNLOAD_TYPE, result)
//                        }
//                    }
//                    .negativeButton(text = "取消")
//                    .checkBoxPrompt(text = "不再显示") {
//                        checked = it
//                    }.show()
//        }
//    }
//
//    private fun initView() {
//        check(wallpapers.isNotEmpty()) { "wallpapers size can not < 1" }
//
//        viewPager.addOnPageChangeListener(this)
//        viewPager.offscreenPageLimit = 4
//        vpAdapter = PictureBrowserVpAdapter(supportFragmentManager, wallpapers, this)
//        viewPager.adapter = vpAdapter
//        viewPager.currentItem = intent.getIntExtra("current", 0)
//        onPageSelected(viewPager.currentItem)
//
//        mbLoadOrigin.setOnClickListener {
//            if (!HkUserManager.isLogin) {
//                launchActivity(Intent(this, LoginActivity::class.java))
//                return@setOnClickListener
//            }
//            //            mbLoadOrigin.isEnabled = false//在结果回调前禁用二次点击
//            vpAdapter.getFragment(viewPager.currentItem).loadPicture(Behavior.LOAD_ORIGIN)
//        }
//        ivDownload.setOnClickListener {
//            if (!HkUserManager.isLogin) {
//                launchActivity(Intent(this@PictureBrowserActivity, LoginActivity::class.java))
//                return@setOnClickListener
//            }
//            showDownloadDialog(curPaper)
//        }
//        ivMore.setOnClickListener {
//            showMenu()
//        }
//
//        tvLike.text = curPaper.collectNum.toString()
//        tvLike.setOnClickListener {
//            if (!HkUserManager.isLogin) {
//                launchActivity(Intent(this, LoginActivity::class.java))
//                return@setOnClickListener
//            }
//            tvLike.isClickable = false
//            mPresenter?.addCollect(curPaper.id, viewPager.currentItem)
//        }
//
//        ivShare.setOnClickListener {
//            mPresenter?.getShareData(curPaper)
//        }
//
//        tvComment.text = curPaper.commentNum.toString()
//        tvComment.setOnClickListener {
////            if (HkUserManager.needLogin(this)) {
////                return@setOnClickListener
////            }
//            showCommentDialog()
//        }
//    }
//
//    override fun resetCollect() {
//        tvLike.isClickable = true
//    }
//
//    override fun showLikeStatus(position: Int) {
//        curPaper.collected = true
//        curPaper.collectNum = curPaper.collectNum + 1
//        if (position == viewPager.currentItem) {
//            tvLike.text = curPaper.collectNum.toString()
//            tvLike.isClickable = false
//            tvLike.setCompoundDrawablesRelativeWithIntrinsicBounds(0
//                    , R.drawable.ic_favorite_black_24dp, 0, 0)
//        }
//        EventBus.getDefault().post(LikeEvent(position))
//    }
//
//    override fun showMessage(message: String) {
//        ToastUtils.showShort(message)
//    }
//
//    override fun launchActivity(intent: Intent) {
//        ArmsUtils.startActivity(intent)
//    }
//
//    override fun killMyself() {
//        ScreenUtils.setNonFullScreen(this)
//        finishAfterTransition()
//    }
//
//    override fun onBackPressed() {
//        ScreenUtils.setNonFullScreen(this)
//        super.onBackPressed()
//    }
//
//    override fun showPictures(pictures: MutableList<Wallpaper>) {
//        wallpapers = pictures as ArrayList<Wallpaper>
//        initView()
//    }
//
//    override fun showDonateDialog() {
//        if (System.currentTimeMillis() - SPUtils.getInstance().getLong(Constant.LAST_SHOW_DONATE) < HkUserManager.user.showDonateInterval) {
//            return
//        }
//        SPUtils.getInstance().put(Constant.LAST_SHOW_DONATE, System.currentTimeMillis())
//        val dialog = MaterialDialog(this).show {
//            customView(R.layout.activity_donate)
//            cancelable(false)
//            cornerRadius(12f)
//        }
//        dialog.getCustomView().findViewById<MaterialButton>(R.id.mbDonate).setOnClickListener {
//            HkUtils.contactKefu()
//        }
//        dialog.getCustomView().findViewById<TextView>(R.id.tvJump).setOnClickListener {
//            dialog.dismiss()
//        }
//    }
//
//    override fun showCommentDialog() {
//        val commentDialog = CommentDialog.newInstance(curPaper.id)
//        commentDialog.setCallback(object : CommentDialog.Callback {
//            override fun commentAdd() {
//                curPaper.commentNum += 1
//                tvComment.text = curPaper.commentNum.toString()
//            }
//        })
//        commentDialog.show(supportFragmentManager, null)
//    }
//
//    override fun onLoadOrigin(pos: Int, result: Boolean) {
//        wallpapers[pos].isOriginExist = result
////        if (pos == viewPager.currentItem) {
////            mbLoadOrigin.visibility = View.GONE
////        }
//    }
//
//    /**
//     * 先于initData执行
//     * */
//    override fun onReturnSceneData(scene: Scene) {
//        paperId = scene.params["id"] as Long
//    }
//
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        setIntent(intent)
//        MobLink.updateNewIntent(getIntent(), this)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        BigImageViewer.imageLoader().cancelAll()
//        callback = null
//    }
//
//    companion object {
//        private var callback: Callback? = null
//
//        fun open(current: Int = 0, callback: Callback? = null, compat: ActivityOptionsCompat? = null,
//                 categoryId: Int = -1, subjectId: Int = -1, context: Context) {
//            Companion.callback = callback
//            ARouter.getInstance()
//                    .build("/picture/browser/activity")
//                    .withInt("categoryId", categoryId)
//                    .withInt("current", current)
//                    .withInt("subjectId", subjectId)
//                    .withOptionsCompat(compat)
//                    .navigation(context)
//        }
//    }
//
//
//    interface Callback {
//        fun getWallpaperList(): MutableList<Wallpaper>
//
//        fun loadMore() {
//
//        }
//
//        fun loadMore(viewPager: ViewPager) {
//
//        }
//    }
//}