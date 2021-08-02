package com.shentu.paper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.micro.base.BaseFragment
import com.micro.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.shentu.paper.R
import com.shentu.paper.app.page.EmptyCallback
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.model.entity.Category
import com.shentu.paper.mvp.contract.CategoryContract
import com.shentu.paper.mvp.presenter.CategoryPresenter
import com.shentu.paper.mvp.ui.adapter.CategoryAdapter
import com.shentu.paper.mvp.ui.adapter.decoration.RvCategoryDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_category.*

@AndroidEntryPoint
class TabCategoryFragment : BaseFragment<CategoryPresenter>(), CategoryContract.View {

    private lateinit var loadService: LoadService<Any>
    private var isLoading = false

    companion object {
        fun newInstance(): TabCategoryFragment {
            return TabCategoryFragment()
        }
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_category, container, false)
        loadService = LoadSir.getDefault().register(rootView) {
            showLoading()
            mPresenter?.getCategories(true)
        }
        return loadService.loadLayout
    }

    override fun initData(savedInstanceState: Bundle?) {
        val lp = smartRefresh.layoutParams as FrameLayout.LayoutParams
        lp.topMargin = BarUtils.getStatusBarHeight()
        smartRefresh.layoutParams = lp

        rvCategory.layoutManager = GridLayoutManager(context, 2)
        rvCategory.addItemDecoration(RvCategoryDecoration(12))
        rvCategory.setHasFixedSize(true)
        rvCategory.adapter = CategoryAdapter(arrayListOf())
        rvCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager: GridLayoutManager = (recyclerView.layoutManager) as GridLayoutManager
                val into = manager.findLastCompletelyVisibleItemPosition()
                val total = manager.itemCount
                if (total - into < 30 && !isLoading) {
                    isLoading = true
                    mPresenter?.getCategories(false)
                }
            }
        })

        smartRefresh.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mPresenter?.getCategories(false)
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                mPresenter?.getCategories(true)
            }
        })
        mPresenter?.getCategories(true)
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

    override fun showNoMoreData() {
        smartRefresh.setNoMoreData(true)
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear) {
            smartRefresh.finishRefresh()
        } else {
            smartRefresh.finishLoadMore()
        }
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        activity?.finish()
    }

    override fun showCategories(results: MutableList<Category>?, clear: Boolean) {
        if (clear) {
            (rvCategory.adapter as CategoryAdapter).setNewData(results)
        } else {
            isLoading = false
            results?.let { (rvCategory.adapter as CategoryAdapter).addData(it) }
        }
    }
}
