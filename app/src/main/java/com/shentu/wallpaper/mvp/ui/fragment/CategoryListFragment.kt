package com.shentu.wallpaper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.shentu.wallpaper.R
import com.shentu.wallpaper.di.component.DaggerCategoryListComponent
import com.shentu.wallpaper.di.module.CategoryListModule
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.CategoryListContract
import com.shentu.wallpaper.mvp.presenter.CategoryListPresenter
import com.shentu.wallpaper.mvp.ui.activity.CategoryPageActivity
import com.shentu.wallpaper.mvp.ui.adapter.CategoryListAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.RvCategoryListDecoration
import kotlinx.android.synthetic.main.fragment_category_list.*
import timber.log.Timber


/**
 * 如果没presenter
 * 你可以这样写
 *
 * @FragmentScope(請注意命名空間) class NullObjectPresenterByFragment
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class CategoryListFragment : BaseFragment<CategoryListPresenter>(), CategoryListContract.View, OnRefreshListener, OnLoadMoreListener {

    private var page = 0
    private var categoryId: Int = 0

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

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        categoryId = arguments?.getInt(CATEGORY_ID)!!
        rvCategoryList.layoutManager = GridLayoutManager(context, 3)
        rvCategoryList.addItemDecoration(RvCategoryListDecoration(2))
//        rvCategoryList.addOnScrollListener(OnGlideScrollListener())
        smartRefresh.setOnRefreshListener(this)
        smartRefresh.setOnLoadMoreListener(this)
        smartRefresh.autoRefresh()
    }

    override fun showCategoryList(wallpapers: MutableList<Wallpaper>) {
        if (rvCategoryList.adapter == null) {
            rvCategoryList.adapter = CategoryListAdapter(wallpapers, categoryId)
            (rvCategoryList.adapter as CategoryListAdapter).onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                Timber.e("Time:"+System.currentTimeMillis())
                ViewCompat.setTransitionName(view, "shareView")
                val options = activity?.let { ActivityOptionsCompat.makeSceneTransitionAnimation(it, view, "shareView") }
                CategoryPageActivity.open(activity, categoryId, position / MicroService.PAGE_LIMIT, position % MicroService.PAGE_LIMIT, options)
            }
        } else {
            (rvCategoryList.adapter as CategoryListAdapter).addData(wallpapers)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout?) {
        page = 0
        mPresenter?.getCategoryList(categoryId, page,true)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout?) {
        mPresenter?.getCategoryList(categoryId, ++page,false)
    }

    override fun setData(data: Any?) {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {
        smartRefresh.finishRefresh()
        smartRefresh.finishLoadMore()
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
