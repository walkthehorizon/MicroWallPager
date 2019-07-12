package com.shentu.wallpaper.mvp.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.jess.arms.mvp.IView
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.Banner
import com.shentu.wallpaper.model.response.BannerPageResponse
import com.shentu.wallpaper.mvp.ui.adapter.BannerListAdapter
import kotlinx.android.synthetic.main.activity_subject_list.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber

class BannerListActivity : BaseActivity<IPresenter>(), IView {

    private var offset: Int = 0
    private var adapter: BannerListAdapter = BannerListAdapter(ArrayList())
    private lateinit var loadService: LoadService<Any>

    override fun setupActivityComponent(appComponent: AppComponent) {

    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_subject_list
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadService = LoadSir.getDefault().register(this) {
            showContent()
            smartRefresh.autoRefresh()
        }

        smartRefresh.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                getBanners(false)
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                getBanners(true)
            }
        })

        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
            val banner = adapter?.data?.get(position) as Banner
            SubjectDetailActivity.open(banner.subjectId, banner.imageUrl)
        }
        rvBanner.layoutManager = LinearLayoutManager(this)
        rvBanner.setHasFixedSize(true)
        rvBanner.adapter = adapter

        getBanners(true)
    }

    private fun showBanners(banners: List<Banner>, clear: Boolean) {
        if (clear) {
            adapter.setNewData(banners)
        } else {
            adapter.addData(banners)
        }
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
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

    private fun getBanners(clear: Boolean) {
        offset = if (clear) 0 else offset + MicroService.PAGE_LIMIT
        ArmsUtils.obtainAppComponentFromContext(this)
                .repositoryManager()
                .obtainRetrofitService(MicroService::class.java)
                .getBanners(MicroService.PAGE_LIMIT, offset)
                .compose(RxUtils.applySchedulers(this, clear))
                .subscribe(object : ErrorHandleSubscriber<BannerPageResponse>(
                        ArmsUtils.obtainAppComponentFromContext(this).rxErrorHandler()) {
                    override fun onNext(t: BannerPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { showBanners(it, clear) }
                    }
                })
    }
}