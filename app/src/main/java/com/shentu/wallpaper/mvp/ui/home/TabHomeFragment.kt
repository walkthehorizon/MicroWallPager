package com.shentu.wallpaper.mvp.ui.home

import android.animation.ArgbEvaluator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.SparseIntArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.google.android.material.appbar.AppBarLayout
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.BaseLazyLoadFragment
import com.jess.arms.mvp.IView
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.Preconditions.checkNotNull
import com.jess.arms.utils.RxLifecycleUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.di.component.DaggerHotPagerComponent
import com.shentu.wallpaper.di.module.TabHomeModule
import com.shentu.wallpaper.model.entity.Banner
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.TabHomeContract
import com.shentu.wallpaper.mvp.presenter.TabHomePresenter
import com.shentu.wallpaper.mvp.ui.activity.SearchActivity
import com.shentu.wallpaper.mvp.ui.adapter.HomeBannerAdapter
import com.shentu.wallpaper.mvp.ui.adapter.RecommendAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.RandomRecommendDecoration
import com.shentu.wallpaper.mvp.ui.widget.CustomPopWindow
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_setting_more.*
import kotlinx.android.synthetic.main.fragment_tab_home.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import java.util.concurrent.TimeUnit


class TabHomeFragment : BaseLazyLoadFragment<TabHomePresenter>(), TabHomeContract.View
        , OnRefreshListener, OnLoadMoreListener, ViewPager.OnPageChangeListener {

    private var popWindow: CustomPopWindow? = null
    private var subType = -1//主题分类
    private var typeSparse: SparseIntArray? = null
    private lateinit var recommendAdapter: RecommendAdapter
    private var loadService: LoadService<*>? = null
    private lateinit var bannerAdapter: HomeBannerAdapter
    private var isLoading: Boolean = false
    private lateinit var appComponent: AppComponent
    private var isLightMode = false
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
                .tabHomeModule(TabHomeModule(this))
                .build()
                .inject(this)
        this.appComponent = appComponent
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_tab_home, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadService = LoadSir.getDefault().register(rvHot)
        //初始化筛选项
        typeSparse = SparseIntArray()
        typeSparse!!.append(R.id.mTvDefault, -1)
        typeSparse!!.append(R.id.mTvWallpaper, 1)
        typeSparse!!.append(R.id.mTvCos, 2)
        typeSparse!!.append(R.id.mTvGirl, 3)

        recommendAdapter = RecommendAdapter(mContext, ArrayList())
        rvHot.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvHot.addItemDecoration(RandomRecommendDecoration(ConvertUtils.dp2px(12.0f)))
        rvHot.adapter = recommendAdapter

        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
            //            Timber.e("current:$i total:${appBarLayout.totalScrollRange} p:${i * 1.0f / appBarLayout.totalScrollRange}")
            val percent: Float = Math.abs(i) * 1.0f / appBarLayout.totalScrollRange//向上滚动，增大
            bgSearch.setBackgroundColor(ArgbEvaluator().evaluate(percent, Color.TRANSPARENT, Color.WHITE) as Int)
            if (percent - 0.5f > 0) {
                activity?.let {
                    isLightMode = true
                    BarUtils.setStatusBarLightMode(it, true)
                }
                val color = Color.parseColor("#666666")
                tvSearch.setTextColor(color)
                tvSearch.supportBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#F5F5F5"))
                tvSearch.supportCompoundDrawablesTintList = ColorStateList.valueOf(color)
            } else {
                activity?.let {
                    isLightMode = false
                    BarUtils.setStatusBarLightMode(it, false)
                }
                val color = Color.parseColor("#AAFDFDFD")
                tvSearch.setTextColor(color)
                tvSearch.supportBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#4FF5F5F5"))
                tvSearch.supportCompoundDrawablesTintList = ColorStateList.valueOf(color)
            }
            arc1.alpha = Math.max(1.0f - percent * 3, 0f)//双倍速度隐藏与显示，效果更好
        })
        ViewCompat.setTransitionName(tvSearch, getString(R.string.search_transitionName))
        tvSearch.setOnClickListener {
            val compact: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this.activity!!, tvSearch, getString(R.string.search_transitionName)
            )
            startActivity(Intent(mContext, SearchActivity::class.java), compact.toBundle())
//            SearchActivity.open(compact)
        }
        rvHot.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager: StaggeredGridLayoutManager = (recyclerView.layoutManager) as StaggeredGridLayoutManager
                val into = manager.findLastVisibleItemPositions(null)
                val total = manager.itemCount
                if (total - into[0] < 12 && !isLoading) {
                    isLoading = true
                    mPresenter?.getRecommends(false)
                    Timber.e("auto load more...")
                }
            }
        })
        showBanners(banners as MutableList)
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
        activity?.finish()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPresenter?.getRecommends(false)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.getRecommends(true)
    }

    override fun showBanners(banners: MutableList<Banner>) {
        bannerAdapter = HomeBannerAdapter(banners)
        bannerPager.offscreenPageLimit = banners.size
        bannerPager.pageMargin = ConvertUtils.dp2px(12.0f)
        bannerPager.adapter = bannerAdapter
        bannerPager.addOnPageChangeListener(this)
        circleIndicator.setViewPager(bannerPager)
        startCountDown()
    }

//    @SuppressLint("CheckResult")
//    override fun showHotSubject(subjects: List<Subject>, clear: Boolean) {
//        for (subject in subjects) {
//            if (TextUtils.isEmpty(subject.cover_1) || TextUtils.isEmpty(subject.cover_2)) {
//                subject.type = Subject.ITEM_VIEW_1
//            }
//        }
//        if (clear) {
//            hotAdapter!!.setNewData(subjects)
//        } else {
//            hotAdapter!!.addData(subjects)
//        }
//    }

    override fun showRecommends(wallpapers: MutableList<Wallpaper>, clear: Boolean) {
        if (clear) {
            recommendAdapter.setNewData(wallpapers)
        } else {
            isLoading = false
            recommendAdapter.addData(wallpapers)
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
//
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //Timber.e("position:$position  offest:$positionOffset  pixel:$positionOffsetPixels")
        val evaluate = ArgbEvaluator().evaluate(positionOffset, Color.parseColor(banners[position].color),
                Color.parseColor(banners[if (position == bannerPager.adapter!!.count - 1) 0 else position + 1].color)) as Int
        arc1.setColorFilter(evaluate)
    }

    override fun onPageSelected(position: Int) {
//        arc1.setImageResource(Color.parseColor(banners[position].color))
    }

    private fun startCountDown() {
        Observable.interval(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(RxLifecycleUtils.bindUntilEvent(this@TabHomeFragment as IView, FragmentEvent.DESTROY))
                .subscribe(object : ErrorHandleSubscriber<Long>(appComponent.rxErrorHandler()) {
                    override fun onNext(t: Long) {
//                        Timber.e("timer:$t")
                        bannerPager.currentItem = (t % bannerAdapter.count).toInt()
                    }
                })
    }

    fun getIsLightMode(): Boolean {
        return isLightMode
    }

    companion object {
        fun newInstance(): TabHomeFragment {
            return TabHomeFragment()
        }
    }
}
