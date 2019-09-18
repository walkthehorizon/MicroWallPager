package com.shentu.wallpaper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
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
import com.shentu.wallpaper.di.component.DaggerCategoryListComponent
import com.shentu.wallpaper.di.module.CategoryListModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.CategoryDetailContract
import com.shentu.wallpaper.mvp.presenter.CategoryListPresenter
import com.shentu.wallpaper.mvp.ui.adapter.CategoryListAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.RvCategoryListDecoration
import com.shentu.wallpaper.mvp.ui.browser.PictureBrowserActivity
import kotlinx.android.synthetic.main.fragment_category_list.*


class CategoryListFragment : BaseFragment<CategoryListPresenter>(), CategoryDetailContract.View
        , OnRefreshListener, OnLoadMoreListener, PictureBrowserActivity.Callback {
    override fun getWallpaperList(): List<Wallpaper> {
        return (rvCategoryList.adapter as CategoryListAdapter).data
    }

    private var categoryId: Int = 0
    private lateinit var loadService: LoadService<Any>

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

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerCategoryListComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .categoryListModule(CategoryListModule(this))
                .build()
                .inject(this)
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
        rvCategoryList.layoutManager = GridLayoutManager(context
                , 3)
        rvCategoryList.addItemDecoration(RvCategoryListDecoration(2))
//        rvCategoryList.addOnScrollListener(OnGlideScrollListener())
        smartRefresh.setOnRefreshListener(this)
        smartRefresh.setOnLoadMoreListener(this)
        smartRefresh.autoRefresh()
    }

    override fun showCategoryList(wallpapers: MutableList<Wallpaper>) {
        if (rvCategoryList.adapter == null) {
            rvCategoryList.adapter = CategoryListAdapter(wallpapers, categoryId)
            (rvCategoryList.adapter as CategoryListAdapter).onItemClickListener = BaseQuickAdapter
                    .OnItemClickListener { _, view, position ->
                        val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view
                                , view.width / 2, view.height / 2
                                , 0, 0)
                        PictureBrowserActivity.open(position, compat = compat, callback = this
                                , context = mContext)
                    }
        } else {
            (rvCategoryList.adapter as CategoryListAdapter).addData(wallpapers)
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

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }
}
