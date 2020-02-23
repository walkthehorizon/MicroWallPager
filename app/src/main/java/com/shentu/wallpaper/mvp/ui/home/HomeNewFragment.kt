package com.shentu.wallpaper.mvp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.TimeUtils
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.jess.arms.mvp.IView
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.WallpaerSection
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.model.response.WallpaperPageResponse
import com.shentu.wallpaper.mvp.ui.adapter.HomeNewestAdapter
import com.shentu.wallpaper.mvp.ui.browser.PictureBrowserActivity
import com.shentu.wallpaper.mvp.ui.widget.stickyHead.StickyItemDecoration
import kotlinx.android.synthetic.main.activity_home_new.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import java.text.SimpleDateFormat
import java.util.*

class HomeNewFragment : BaseFragment<IPresenter>(), IView {

    private lateinit var adapter: HomeNewestAdapter
    private lateinit var appComponent: AppComponent
    private lateinit var loadService: LoadService<Any>
    private var bViewPager: ViewPager? = null

    override fun setupFragmentComponent(appComponent: AppComponent) {
        this.appComponent = appComponent
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_home_new, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadService = LoadSir.getDefault().register(smartRefresh) {
            showLoading()
            getNewest(true)
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
            context?.let {
                PictureBrowserActivity.open(position, object : PictureBrowserActivity.Callback {
                    override fun getWallpaperList(): MutableList<Wallpaper> {
                        return papers
                    }

                    override fun loadMore(viewPager: ViewPager) {
                        bViewPager = viewPager
                        getNewest(false)
                    }

                }, compat, context = it)
            }
        }

        stickyHead.setDataCallback { pos ->
            stickyHead.findViewById<TextView>(R.id.tvTime).text = adapter.data[pos].header
        }
        rvNew.layoutManager = LinearLayoutManager(context)
        rvNew.addItemDecoration(StickyItemDecoration(stickyHead, adapter.getHeadType()))
        rvNew.adapter = adapter

        getNewest(true)
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
    private var papers: MutableList<Wallpaper> = mutableListOf()

    private fun getNewest(clear: Boolean) {
        offset = if (clear) MicroService.PAGE_START else offset + MicroService.PAGE_LIMIT
        if (clear) {
            papers.clear()
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
                        val newestList = mutableListOf<WallpaerSection>()
                        var curDate = if (adapter.data.isEmpty()) "" else adapter.data[adapter.data.size - 1].t?.created
                        for (paper in t.data.content) {
                            if (paper.created != curDate) {
                                newestList.add(WallpaerSection(true, if (isToday(paper.created)) "今天" else paper.created))
                                curDate = paper.created
                                continue
                            }
                            newestList.add(WallpaerSection(paper))
                        }
                        if (clear) {
                            papers = t.data.content
                        } else {
                            papers.addAll(t.data.content)
                        }
                        if (clear) {
                            adapter.setNewData(newestList)
                        } else {
                            adapter.addData(newestList)
                        }
                        if (papers.size > 0) {
                            bViewPager?.adapter?.notifyDataSetChanged()
                        }
                    }
                })
    }

    private fun isToday(date: String): Boolean {
        return TimeUtils.isToday(TimeUtils.string2Millis(date, SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)))
    }

    companion object {
        fun newInstance(): HomeNewFragment {
            return HomeNewFragment()
        }
    }
}