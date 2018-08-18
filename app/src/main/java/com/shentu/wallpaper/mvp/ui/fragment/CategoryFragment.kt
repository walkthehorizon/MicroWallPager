package com.shentu.wallpaper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blankj.utilcode.util.BarUtils

import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.R



import com.shentu.wallpaper.di.module.CategoryModule
import com.shentu.wallpaper.mvp.contract.CategoryContract
import com.shentu.wallpaper.di.component.DaggerCategoryComponent
import com.shentu.wallpaper.model.entity.Category
import com.shentu.wallpaper.mvp.presenter.CategoryPresenter
import com.shentu.wallpaper.mvp.ui.adapter.CategoryAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.RvCategoryDecoration
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_category.view.*


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
class CategoryFragment : BaseFragment<CategoryPresenter>(), CategoryContract.View {

    companion object {
        fun newInstance(): CategoryFragment {
            val fragment = CategoryFragment()
            return fragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //填充statusBar
        val lp = statusView.layoutParams as LinearLayout.LayoutParams
        lp.height = BarUtils.getStatusBarHeight()
        statusView.layoutParams = lp
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        rvCategory.layoutManager = GridLayoutManager(context, 3)
        rvCategory.addItemDecoration(RvCategoryDecoration(12))
        rvCategory.setHasFixedSize(true)
        mPresenter?.getCategorys()
    }

    override fun setData(data: Any?) {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

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

    override fun showCategorys(results: MutableList<Category>?) {
        rvCategory.adapter = CategoryAdapter(results)
    }
}
