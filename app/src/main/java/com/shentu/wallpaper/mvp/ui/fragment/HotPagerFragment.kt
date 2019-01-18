package com.shentu.wallpaper.mvp.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.SparseIntArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.BarUtils
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.BaseLazyLoadFragment
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.Preconditions.checkNotNull
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.di.component.DaggerHotPagerComponent
import com.shentu.wallpaper.di.module.HotPagerModule
import com.shentu.wallpaper.model.entity.Subject
import com.shentu.wallpaper.mvp.contract.HotPagerContract
import com.shentu.wallpaper.mvp.presenter.HotPagerPresenter
import com.shentu.wallpaper.mvp.ui.adapter.HotAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.HotPageRvDecoration
import com.shentu.wallpaper.mvp.ui.widget.CustomPopWindow
import kotlinx.android.synthetic.main.activity_setting_more.*
import kotlinx.android.synthetic.main.fragment_hot_pager.*


class HotPagerFragment : BaseLazyLoadFragment<HotPagerPresenter>(), HotPagerContract.View, OnRefreshListener, OnLoadMoreListener {

    private var popWindow: CustomPopWindow? = null
    private var subType = -1//主题分类
    private var typeSparse: SparseIntArray? = null
    private var hotAdapter: HotAdapter? = null
    private var loadService: LoadService<*>? = null

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerHotPagerComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .hotPagerModule(HotPagerModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        loadService = LoadSir.getDefault().register(inflater.inflate(R.layout.fragment_hot_pager, container, false))
        return loadService!!.loadLayout
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadService?.showSuccess()
        //初始化筛选项
        typeSparse = SparseIntArray()
        typeSparse!!.append(R.id.mTvDefault, -1)
        typeSparse!!.append(R.id.mTvWallpaper, 1)
        typeSparse!!.append(R.id.mTvCos, 2)
        typeSparse!!.append(R.id.mTvGirl, 3)

        hotAdapter = HotAdapter(null)
        rvHot.layoutManager = LinearLayoutManager(context)
        rvHot.addItemDecoration(HotPageRvDecoration(8))
        rvHot.adapter = hotAdapter
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
//        toolbar.setTitle(resources.getString(R.string.app_name))
//        toolbar.setRightIcon(R.drawable.ic_more_horiz_white_24dp)
//        toolbar.setOnClickListener(object : DefaultToolbar.OnClickListenerImpl() {
//            override fun onClickRightIcon() {
//                showFilterPop()
//            }
//        })
        val lp = toolbarHot.layoutParams as AppBarLayout.LayoutParams
        lp.topMargin = BarUtils.getStatusBarHeight()
        toolbarHot.layoutParams = lp
    }

    override fun setData(data: Any?) {

    }

    override fun lazyLoadData() {
        refreshLayout.autoRefresh()
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear)
            refreshLayout.finishRefresh()
        else
            refreshLayout.finishLoadMore()
    }

    override fun showEmpty() {
        loadService!!.showCallback(EmptyCallback::class.java)
    }

    override fun showContent() {
        loadService!!.showSuccess()
    }

    override fun showError() {
        loadService!!.showCallback(ErrorCallback::class.java)
    }


    override fun showMessage(message: String) {
        checkNotNull(message)
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        assert(mPresenter != null)
        mPresenter!!.getSubjects(subType, false)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        assert(mPresenter != null)
        mPresenter!!.getSubjects(subType, true)
    }

    @SuppressLint("CheckResult")
    override fun showHotSubject(subjects: List<Subject>, clear: Boolean) {
        for (subject in subjects) {
            if (TextUtils.isEmpty(subject.cover_1)|| TextUtils.isEmpty(subject.cover_2)){
                subject.type = Subject.ITEM_VIEW_1
            }
        }
        if (clear) {
            hotAdapter!!.setNewData(subjects)
        } else {
            hotAdapter!!.addData(subjects)
        }
    }

    override fun showFilterPop() {
        if (popWindow == null) {
            val contentView = LayoutInflater.from(context).inflate(R.layout.layout_popwindow_filter_hot, null) as ViewGroup
            val listener = { view: View ->
                if (popWindow != null) {
                    popWindow!!.dissmiss()
                }
                if (mPresenter != null && subType != typeSparse?.get(view.id)) {
                    contentView.findViewById<View>(R.id.mTvDefault).isSelected = false
                    contentView.findViewById<View>(R.id.mTvWallpaper).isSelected = false
                    contentView.findViewById<View>(R.id.mTvCos).isSelected = false
                    contentView.findViewById<View>(R.id.mTvGirl).isSelected = false
                    view.isSelected = true
                    subType = typeSparse!!.get(view.id)
                    refreshLayout.autoRefresh()
                }
            }
            contentView.findViewById<View>(R.id.mTvDefault).setOnClickListener(listener)
            contentView.findViewById<View>(R.id.mTvDefault).isSelected = true
            contentView.findViewById<View>(R.id.mTvWallpaper).setOnClickListener(listener)
            contentView.findViewById<View>(R.id.mTvCos).setOnClickListener(listener)
            contentView.findViewById<View>(R.id.mTvGirl).setOnClickListener(listener)
            popWindow = CustomPopWindow.PopupWindowBuilder(context)
                    .setView(contentView)
                    .setAnimationStyle(R.style.QMUI_Animation_PopDownMenu_Right)
                    .create()
                    .showAsDropDown(toolbar, 0, 0, Gravity.END)
        } else {
            popWindow!!.showAsDropDown(toolbar, 0, 0, Gravity.END)
        }
    }

    companion object {

        fun newInstance(): HotPagerFragment {
            return HotPagerFragment()
        }
    }
}
