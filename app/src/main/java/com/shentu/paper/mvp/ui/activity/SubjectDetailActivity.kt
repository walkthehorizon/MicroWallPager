package com.shentu.paper.mvp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.paper.R
import com.shentu.paper.app.GlideArms
import com.shentu.paper.app.page.EmptyCallback
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.app.page.LoadingCallback
import com.shentu.paper.di.component.DaggerSubjectDetailComponent
import com.shentu.paper.di.module.SubjectDetailModule
import com.shentu.paper.model.entity.Banner
import com.shentu.paper.model.entity.Subject
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.mvp.contract.SubjectDetailContract
import com.shentu.paper.mvp.presenter.SubjectDetailPresenter
import com.shentu.paper.mvp.ui.adapter.SubjectDetailAdapter
import com.shentu.paper.mvp.ui.browser.PictureBrowserActivity
import kotlinx.android.synthetic.main.activity_subject_detail.*

@Route(path = "/activity/subject/detail")
class SubjectDetailActivity : BaseActivity<SubjectDetailPresenter>(), SubjectDetailContract.View {

    @Autowired
    @JvmField
    var banner: Banner? = null
    @Autowired
    @JvmField
    var subjectId = -1
    @Autowired
    @JvmField
    var type = 1

    private lateinit var adapter: SubjectDetailAdapter
    private lateinit var loadService: LoadService<Any>
    private var bViewPager: ViewPager? = null

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerSubjectDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .subjectDetailModule(SubjectDetailModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_subject_detail
    }


    override fun initData(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        loadService = LoadSir.getDefault().register(this) {
            showLoading()
            loadData(false)
        }
        if (type == 1) {
            smartRefresh.setEnableRefresh(true)
            smartRefresh.setEnableLoadMore(true)
            smartRefresh.setOnRefreshListener {
                loadData(false)
            }
        }
        adapter = SubjectDetailAdapter(emptyList())
        adapter.setOnItemClickListener { _, view, position ->
            val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view
                    , view.width / 2, view.height / 2
                    , 0, 0)
            PictureBrowserActivity.open(position, object : PictureBrowserActivity.Callback {
                override fun getWallpaperList(): MutableList<Wallpaper> {
                    return adapter.data
                }

                override fun loadMore(viewPager: ViewPager) {
                    bViewPager = viewPager
                    loadData(false)
                }

            }, compat = compat, context = this)
        }
        rvSubject.layoutManager = GridLayoutManager(this, 2)
        rvSubject.setHasFixedSize(true)
        rvSubject.adapter = adapter

        loadData(true)
    }

    private fun loadData(clear: Boolean) {
        if (type == 1) {
            mPresenter?.getSubjectDetail(subjectId)
            mPresenter?.getSubjectWallpapers(subjectId, clear)
        } else {
            toolbar.setTitle(banner?.title)
            mPresenter?.getBannerWallpapers(banner!!.id)
            ivCover.visibility = VISIBLE
            GlideArms.with(this)
                    .load(banner?.imageUrl)
                    .error(R.drawable.default_cover_horizon)
                    .into(ivCover)
        }
    }

    override fun showDetail(subject: Subject) {
        toolbar.setTitle(subject.name)
        tvDesc.loadData(subject.description, "text/html", "utf-8")
    }

    override fun showWallpapers(wallpapers: List<Wallpaper>, clear: Boolean) {
        if (clear) {
            adapter.setNewData(wallpapers)
        } else {
            adapter.addData(wallpapers)
        }
        if (wallpapers.isNotEmpty()) {
            bViewPager?.adapter?.notifyDataSetChanged()
        }
    }

    override fun showLoading() {
        loadService.showCallback(LoadingCallback::class.java)
    }

    override fun showContent() {
        loadService.showSuccess()
    }

    override fun showEmpty() {
        loadService.showCallback(EmptyCallback::class.java)
    }

    override fun showError() {
        loadService.showCallback(ErrorCallback::class.java)
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }

    companion object {

        fun open(subjectId: Int = -1, context: Context) {
            ARouter.getInstance()
                    .build("/activity/subject/detail")
                    .withInt("subjectId", subjectId)
                    .withInt("type", 1)
                    .navigation(context)
        }

        fun open(banner: Banner, context: Context) {
            ARouter.getInstance()
                    .build("/activity/subject/detail")
                    .withSerializable("banner", banner)
                    .withInt("type", 2)
                    .navigation(context)
        }
    }
}
