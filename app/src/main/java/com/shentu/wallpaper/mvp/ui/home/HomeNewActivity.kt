package com.shentu.wallpaper.mvp.ui.home

import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.jess.arms.mvp.IView
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.app.page.LoadingCallback
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.WallpaerSection
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.ui.adapter.HomeNewestAdapter
import com.shentu.wallpaper.mvp.ui.browser.PictureBrowserActivity
import com.shentu.wallpaper.mvp.ui.widget.stickyHead.StickyItemDecoration
import kotlinx.android.synthetic.main.activity_home_new.*
import kotlinx.android.synthetic.main.item_home_newest_section.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import pl.droidsonroids.relinker.elf.Elf
import java.text.SimpleDateFormat
import java.util.*

class HomeNewActivity : BaseActivity<IPresenter>(), IView, PictureBrowserActivity.Callback {

    private lateinit var adapter: HomeNewestAdapter
    private lateinit var appComponent: AppComponent
    private lateinit var loadService: LoadService<Any>

    override fun setupActivityComponent(appComponent: AppComponent) {
        this.appComponent = appComponent
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_home_new
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadService = LoadSir.getDefault().register(smartRefresh) {
            showContent()
            smartRefresh.autoRefresh()
        }

        smartRefresh.setOnRefreshListener {
            getNewest(true)
        }
        smartRefresh.setOnLoadMoreListener {
            getNewest(false)
        }

        adapter = HomeNewestAdapter(mutableListOf())
        adapter.setOnItemClickListener { _, view, position ->
            val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view
                    , view.width / 2, view.height / 2
                    , 0, 0)
            PictureBrowserActivity.open(position, this, compat, context = this)
        }

        stickyHead.setDataCallback { pos ->
            tvTime.text = adapter.data[pos].header
        }
        rvNew.layoutManager = LinearLayoutManager(this)
        rvNew.addItemDecoration(StickyItemDecoration(stickyHead, adapter.getHeadType()))
        rvNew.adapter = adapter

        getNewest(true)
    }

    override fun getWallpaperList(): List<Wallpaper> {
        return papers
    }

    override fun showEmpty() {
        loadService.showCallback(EmptyCallback::class.java)
    }

    override fun showContent() {
        loadService.showSuccess()
    }

    override fun showError() {
        loadService.showCallback(ErrorCallback::class.java)
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear) {
            smartRefresh.finishRefresh()
        } else {
            smartRefresh.finishLoadMore()
        }
    }

    private var offset: Int = MicroService.PAGE_START
    private var curDate = ""
    private var papers: MutableList<Wallpaper> = mutableListOf()
    private var sections  = mutableListOf<WallpaerSection>()

    private fun getNewest(clear: Boolean) {
        offset = if (clear) MicroService.PAGE_START else offset+MicroService.PAGE_LIMIT
        if (clear) {
            curDate = ""
            sections.clear()
        }
        appComponent.repositoryManager()
                .obtainRetrofitService(MicroService::class.java)
                .getNewestPapers(MicroService.PAGE_LIMIT, offset)
                .compose(RxUtils.applySchedulers(this, clear))
                .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(appComponent.rxErrorHandler()) {
                    override fun onNext(t: WallpaperPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        if (t.data == null || t.data.content.isEmpty()) {
                            return
                        }
                        for (paper in t.data.content) {
                            if (TimeUtils.isToday(TimeUtils.string2Millis(paper.created
                                            , SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)))) {
                                paper.created = "今天"
                            }
                        }
                        val sections = adapter.data
                        for (paper in t.data.content) {
                            if (paper.created != curDate) {
                                curDate = paper.created
                                sections.add(WallpaerSection(true, curDate))
                                continue
                            }
                            sections.add(WallpaerSection(paper))
                        }
                        if (clear) {
                            papers = t.data.content
                        } else {
                            papers.addAll(t.data.content)
                        }
                        adapter.notifyDataSetChanged()
                    }
                })
    }
}