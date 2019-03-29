package com.shentu.wallpaper.mvp.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseIntArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ConvertUtils
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
import com.shentu.wallpaper.model.entity.Banner
import com.shentu.wallpaper.model.entity.Subject
import com.shentu.wallpaper.mvp.contract.HotPagerContract
import com.shentu.wallpaper.mvp.presenter.HotPagerPresenter
import com.shentu.wallpaper.mvp.ui.adapter.HomeBannerAdapter
import com.shentu.wallpaper.mvp.ui.adapter.HotAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.HotPageRvDecoration
import com.shentu.wallpaper.mvp.ui.widget.CustomPopWindow
import kotlinx.android.synthetic.main.activity_setting_more.*
import kotlinx.android.synthetic.main.fragment_tab_home.*
import timber.log.Timber


class TabHomeFragment : BaseLazyLoadFragment<HotPagerPresenter>(), HotPagerContract.View
        , OnRefreshListener, OnLoadMoreListener, ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

    private var popWindow: CustomPopWindow? = null
    private var subType = -1//主题分类
    private var typeSparse: SparseIntArray? = null
    private var hotAdapter: HotAdapter? = null
    private var loadService: LoadService<*>? = null
    private lateinit var adapter: HomeBannerAdapter
    private val banners: List<Banner> = listOf(
            Banner("http://c3.res.meizu.com/fileserver/operation/speical/logo/239/1e04b673beda485d9c7befba71aca774.png", "#780000"),
            Banner("http://c3.res.meizu.com/fileserver/operation/speical/logo/239/15802f27083a448ea21ff96bbcce3369.jpg", "#1848C0"),
            Banner("http://c5.res.meizu.com/fileserver/operation/speical/logo/239/2bdcd0d1b8a3477084a2200d8f0d5ddc.jpg", "#F0F0F0"),
            Banner("http://c6.res.meizu.com/fileserver/operation/speical/logo/239/8d961eb8ce1a421f895eb0d7870ede60.jpg", "#1860A8"),
            Banner("http://c3.res.meizu.com/fileserver/operation/speical/logo/239/322d65d6a7a3464a87499c9bb7f390ca.jpg", "#183030"),
            Banner("http://c6.res.meizu.com/fileserver/operation/speical/logo/239/869d2af5e3234ea9887d4c5cd34ce6c6.jpg", "#F0D8D8"),
            Banner("http://c3.res.meizu.com/fileserver/operation/speical/logo/239/898f813dfce74d37b102e12a364457d8.jpg", "#48F0FF"))

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerHotPagerComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .hotPagerModule(HotPagerModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        loadService = LoadSir.getDefault().register(inflater.inflate(R.layout.fragment_tab_home, container, false))
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
        rvHot.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
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
        adapter = HomeBannerAdapter(banners)
        bannerPager.offscreenPageLimit = banners.size + 2
        bannerPager.pageMargin = ConvertUtils.dp2px(12.0f)
        bannerPager.adapter = adapter
        bannerPager.addOnPageChangeListener(this)
        bannerPager.setPageTransformer(true, this)
//        bannerPager.setCurrentItem(adapter.getFirstPosition(, false)
        circleIndicator.setViewPager(bannerPager)
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
            if (TextUtils.isEmpty(subject.cover_1) || TextUtils.isEmpty(subject.cover_2)) {
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

    override fun onPageScrollStateChanged(state: Int) {
//        if (state == ViewPager.SCROLL_STATE_IDLE) {
//            arc1.setImageResource(Color.parseColor(banners[viewPager.currentItem].color))
//        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

//        Timber.e("position:$position  offest:$positionOffset  pixel:$positionOffsetPixels")
//
//        arc1.alpha = positionOffset
//        arc2.alpha = 1 - positionOffset
    }

    override fun onPageSelected(position: Int) {
//        arc1.setImageResource(Color.parseColor(banners[position].color))
    }

    companion object {

        fun newInstance(): TabHomeFragment {
            return TabHomeFragment()
        }
    }

    override fun transformPage(page: View, position: Float) {
        Timber.e("%s%s", page.id, "position:$position")
//        if (position < -1 || position > 1) {
//
//        } else {
//            if (-1 <= position && position < 0) {
//                arc1.alpha = 1 + position - 0f * position
//            } else if (0 < position && position <= 1) {
//                arc1.alpha = 1 - position + 0f * position
//            } else {
//                arc1.alpha = 1f
//                arc2.alpha = 0f
//            }
//        }
    }
}
