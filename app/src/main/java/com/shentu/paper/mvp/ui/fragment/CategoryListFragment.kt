package com.shentu.paper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.micro.base.BaseFragment
import com.micro.utils.ArmsUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.shentu.paper.R
import com.shentu.paper.app.page.EmptyCallback
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.mvp.contract.CategoryDetailContract
import com.shentu.paper.mvp.presenter.CategoryListPresenter
import com.shentu.paper.mvp.ui.adapter.CategoryListAdapter
import com.shentu.paper.mvp.ui.adapter.decoration.RvCategoryListDecoration
import com.shentu.paper.mvp.ui.browser.PaperBrowserActivity
import com.shentu.paper.mvp.ui.browser.SourceCategory
import kotlinx.android.synthetic.main.fragment_category_list.*


class CategoryListFragment : BaseFragment<CategoryListPresenter>(), CategoryDetailContract.View
        , OnRefreshListener, OnLoadMoreListener {

    private var categoryId: Int = 0
    private lateinit var loadService: LoadService<Any>
    private var bViewPager: ViewPager? = null
    private lateinit var adapter: CategoryListAdapter

    companion object {
        const val CATEGORY_ID: String = "category_id"

        fun newInstance(id: Int): CategoryListFragment {
            val fragment = CategoryListFragment()
            val bundle = Bundle()
            bundle.putInt(CATEGORY_ID, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_category_list, container, false)
        loadService = LoadSir.getDefault()
                .register(root) {
                    showContent()
                    smartRefresh.autoRefresh()
                }
        return loadService.loadLayout
    }

    override fun initData(savedInstanceState: Bundle?) {
        categoryId = arguments?.getInt(CATEGORY_ID)!!
        adapter = CategoryListAdapter()
        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, view, position ->
            val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view
                    , view.width / 2, view.height / 2
                    , 0, 0)
            PaperBrowserActivity.open(requireContext(),SourceCategory(position,adapter.data[position].categoryId))
        }
        rvCategoryList.adapter = adapter
        rvCategoryList.layoutManager = GridLayoutManager(context
                , 3)
        rvCategoryList.addItemDecoration(RvCategoryListDecoration(2))
//        rvCategoryList.addOnScrollListener(OnGlideScrollListener())
        smartRefresh.setOnRefreshListener(this)
        smartRefresh.setOnLoadMoreListener(this)
        smartRefresh.autoRefresh()
    }

    override fun showCategoryList(wallpapers: MutableList<Wallpaper> , clear: Boolean) {
        if (clear) {
            adapter.setNewData(wallpapers)
        } else {
            adapter.addData(wallpapers)
        }
        if (wallpapers.isNotEmpty()) {
            bViewPager?.adapter?.notifyDataSetChanged()
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getCategoryList(categoryId, true)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getCategoryList(categoryId, false)
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

    override fun showNoMoreData() {
        smartRefresh.setNoMoreData(true)
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear) {
            smartRefresh.finishRefresh()
        } else {
            smartRefresh.finishLoadMore()
        }
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }
}
