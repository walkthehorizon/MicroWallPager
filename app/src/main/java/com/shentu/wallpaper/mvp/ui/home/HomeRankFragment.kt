package com.shentu.wallpaper.mvp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.di.component.DaggerHomeRankComponent
import com.shentu.wallpaper.di.module.HomeRankModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.HomeRankContract
import com.shentu.wallpaper.mvp.presenter.HomeRankPresenter
import com.shentu.wallpaper.mvp.ui.adapter.HomeRankAdapter
import kotlinx.android.synthetic.main.activity_home_new.smartRefresh
import kotlinx.android.synthetic.main.fragment_home_rank.*


class HomeRankFragment : BaseFragment<HomeRankPresenter>(), HomeRankContract.View {

    private lateinit var loadService: LoadService<Any>
    private lateinit var adapter: HomeRankAdapter


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerHomeRankComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .homeRankModule(HomeRankModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home_rank, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadService = LoadSir.getDefault().register(smartRefresh) {
            showLoading()
            mPresenter?.getRankPapers(true)
        }

        smartRefresh.setOnRefreshListener { mPresenter?.getRankPapers(true) }
        smartRefresh.setOnLoadMoreListener { mPresenter?.getRankPapers(false) }

        mPresenter?.getRankPapers(true)
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

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun showRanks(papers: MutableList<Wallpaper>, clear: Boolean) {
        if (clear) {
            if (!this::adapter.isInitialized) {
                adapter = HomeRankAdapter(papers)
                rvRank.layoutManager = LinearLayoutManager(context)
                rvRank.setHasFixedSize(true)
                rvRank.adapter = adapter
            }
            adapter.setNewData(papers)
        } else {
            adapter.addData(papers)
        }
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
