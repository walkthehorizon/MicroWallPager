package com.shentu.wallpaper.mvp.ui.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.app.page.LoadingCallback
import com.shentu.wallpaper.di.component.DaggerCategoryComponent
import com.shentu.wallpaper.di.module.CategoryModule
import com.shentu.wallpaper.model.entity.Category
import com.shentu.wallpaper.mvp.contract.CategoryContract
import com.shentu.wallpaper.mvp.presenter.CategoryPresenter
import com.shentu.wallpaper.mvp.ui.adapter.CategoryAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.RvCategoryDecoration
import kotlinx.android.synthetic.main.fragment_category.*


class CategoryFragment : BaseFragment<CategoryPresenter>(), CategoryContract.View, OnRefreshListener, OnLoadMoreListener {

    private lateinit var loadService: LoadService<Any>

    companion object {
        fun newInstance(): CategoryFragment {
            return CategoryFragment()
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
        loadService = LoadSir.getDefault().register(rootView) { loadService.showCallback(LoadingCallback::class.java) }
        return loadService.loadLayout
    }

    override fun initData(savedInstanceState: Bundle?) {
        rvCategory.layoutManager = GridLayoutManager(context, 3)
        rvCategory.addItemDecoration(RvCategoryDecoration(12))
        rvCategory.setHasFixedSize(true)
        rvCategory.adapter = CategoryAdapter(null)
        mPresenter?.getCategories(true)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getCategories(true)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getCategories(false)
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
            results?.let { (rvCategory.adapter as CategoryAdapter).addData(it) }
        }
    }
}
