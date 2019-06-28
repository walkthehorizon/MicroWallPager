package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.app.page.LoadingCallback
import com.shentu.wallpaper.di.component.DaggerSubjectDetailComponent
import com.shentu.wallpaper.di.module.SubjectDetailModule
import com.shentu.wallpaper.model.entity.Banner
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.SubjectDetailContract
import com.shentu.wallpaper.mvp.presenter.SubjectDetailPresenter
import com.shentu.wallpaper.mvp.ui.adapter.SubjectDetailAdapter
import kotlinx.android.synthetic.main.activity_subject_detail.*

@Route(path = "/activity/subject/detail")
class SubjectDetailActivity : BaseActivity<SubjectDetailPresenter>(), SubjectDetailContract.View {

    @Autowired
    @JvmField
    var banner: Banner = Banner()

    private lateinit var adapter: SubjectDetailAdapter
    private lateinit var loadService: LoadService<Any>

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerSubjectDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .subjectDetailModule(SubjectDetailModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_subject_detail //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {
        loadService = LoadSir.getDefault().register(this) {
            showLoading()
            mPresenter?.getWallpapers(banner.subjectId)
        }
        ARouter.getInstance().inject(this)
        GlideArms.with(this)
                .load(banner.imageUrl)
                .into(ivCover)
        toolbar.setTitle(banner.title)
        tvDesc.text = banner.desc
        mPresenter?.getWallpapers(banner.subjectId)
    }

    override fun showWallpapers(wallpapers: List<Wallpaper>) {
        adapter = SubjectDetailAdapter(wallpapers)
        adapter.setOnItemClickListener { _, view, position ->
            val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view
                    , view.width / 2, view.height / 2
                    , 0, 0)
            PictureBrowserActivity.open(this@SubjectDetailActivity, wallpapers, position, compat)
        }
        rvSubject.layoutManager = GridLayoutManager(this, 2)
        rvSubject.setHasFixedSize(true)
        rvSubject.adapter = adapter
    }

    override fun showLoading() {
        loadService.showCallback(LoadingCallback::class.java)
    }

    override fun showContent() {
        loadService.showSuccess()
    }

    override fun showError() {
        loadService.showCallback(ErrorCallback::class.java)
    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }

    companion object {
        fun open(banner: Banner) {
            ARouter.getInstance()
                    .build("/activity/subject/detail")
                    .withSerializable("banner", banner)
                    .navigation()
        }
    }
}
