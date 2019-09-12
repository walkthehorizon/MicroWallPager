package com.shentu.wallpaper.mvp.ui.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.di.component.DaggerCategoryComponent
import com.shentu.wallpaper.di.module.CategoryModule
import com.shentu.wallpaper.model.entity.Category
import com.shentu.wallpaper.mvp.contract.CategoryContract
import com.shentu.wallpaper.mvp.presenter.CategoryPresenter
import com.shentu.wallpaper.mvp.ui.adapter.CategoryAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.RvCategoryDecoration
import kotlinx.android.synthetic.main.fragment_category.*


class TabCategoryFragment : BaseFragment<CategoryPresenter>(), CategoryContract.View {

    private lateinit var loadService: LoadService<Any>
    private var isLoading = false

    companion object {
        fun newInstance(): TabCategoryFragment {
            return TabCategoryFragment()
        }
    }


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerCategoryComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .categoryModule(CategoryModule(this))
                .build()
                .inject(this)
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

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
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
