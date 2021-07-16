package com.shentu.paper.mvp.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jess.arms.base.BaseActivity
import com.jess.arms.integration.RepositoryManager
import com.jess.arms.mvp.IPresenter
import com.jess.arms.mvp.IView
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.paper.R
import com.shentu.paper.app.page.EmptyCallback
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.entity.Banner
import com.shentu.paper.model.response.BannerPageResponse
import com.shentu.paper.mvp.ui.adapter.BannerListAdapter
import kotlinx.android.synthetic.main.activity_subject_list.*
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject

class BannerListActivity : BaseActivity<IPresenter>(), IView {

    private var offset: Int = 0
    private var adapter: BannerListAdapter = BannerListAdapter(ArrayList())
    private lateinit var loadService: LoadService<Any>

    @Inject
    lateinit var repositoryManager: RepositoryManager

    @Inject
    lateinit var errorHandler: RxErrorHandler

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_subject_list
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadService = LoadSir.getDefault().register(smartRefresh) {
            showLoading()
            getBanners(true)
        }
        smartRefresh.setOnLoadMoreListener {
            getBanners(false)
        }

        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
            val banner = adapter?.data?.get(position) as Banner
            SubjectDetailActivity.open(banner, this)
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
        repositoryManager
            .obtainRetrofitService(MicroService::class.java)
            .getBanners1()
            .compose(RxUtils.applySchedulers(this, clear))
            .subscribe(object : ErrorHandleSubscriber<BannerPageResponse>(
                errorHandler) {
                override fun onNext(t: BannerPageResponse) {
                    if (!t.isSuccess) {
                        return
                    }
                    t.data?.content?.let { showBanners(it, clear) }
                }
            })
    }
}