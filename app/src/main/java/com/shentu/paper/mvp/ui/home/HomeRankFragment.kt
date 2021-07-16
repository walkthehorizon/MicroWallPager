package com.shentu.paper.mvp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseFragment
import com.jess.arms.integration.RepositoryManager
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.paper.R
import com.shentu.paper.app.page.EmptyCallback
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.WallpaperPageResponse
import com.shentu.paper.mvp.contract.HomeRankContract
import com.shentu.paper.mvp.ui.adapter.HomeRankAdapter
import com.shentu.paper.mvp.ui.browser.PictureBrowserActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_home_new.smartRefresh
import kotlinx.android.synthetic.main.fragment_home_rank.*
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject

/**
 * 最热
 * */
class HomeRankFragment : BaseFragment<IPresenter>(), HomeRankContract.View {

    private lateinit var loadService: LoadService<Any>
    private lateinit var adapter: HomeRankAdapter
    private var bViewPager: ViewPager? = null

    @Inject
    lateinit var repositoryManager: RepositoryManager

    @Inject
    lateinit var errorHandler:RxErrorHandler

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home_rank, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadService = LoadSir.getDefault().register(smartRefresh) {
            showLoading()
            getRankPapers(true)
        }

        smartRefresh.setOnRefreshListener { getRankPapers(true) }
        smartRefresh.setOnLoadMoreListener { getRankPapers(false) }

        getRankPapers(true)
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

    override fun showNoMoreData() {
        smartRefresh.setNoMoreData(true)
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun showRanks(papers: MutableList<Wallpaper>, clear: Boolean) {
        if (clear) {
            if (!this::adapter.isInitialized) {
                adapter = HomeRankAdapter(papers)
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
                                getRankPapers(false)
                            }

                        }, compat, context = it)
                    }
                }
                rvRank.layoutManager = LinearLayoutManager(context)
                rvRank.setHasFixedSize(true)
                rvRank.adapter = adapter
            }
            adapter.setNewData(papers)
        } else {
            adapter.addData(papers)
        }
        if (papers.size > 0) {
            bViewPager?.adapter?.notifyDataSetChanged()
        }
    }

    private var offset = 0;

    fun getRankPapers(clear: Boolean) {
        repositoryManager
            .obtainRetrofitService(MicroService::class.java)
            .getRankPapers(MicroService.PAGE_LIMIT ,
                (if (clear) MicroService.PAGE_START else offset + MicroService.PAGE_START).also {
                    offset = it
                })
            .compose(RxUtils.applySchedulers(this, clear))
            .subscribe(object : ErrorHandleSubscriber<WallpaperPageResponse>(errorHandler) {
                override fun onNext(t: WallpaperPageResponse) {
                    if (!t.isSuccess) {
                        return
                    }
                    t.data?.content?.let { showRanks(it, clear) }
                }
            })
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        activity?.finish()
    }

    companion object {
        fun newInstance(): HomeRankFragment {
            return HomeRankFragment()
        }
    }
}
