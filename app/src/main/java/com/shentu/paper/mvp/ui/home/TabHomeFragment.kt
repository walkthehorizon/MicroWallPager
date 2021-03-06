package com.shentu.paper.mvp.ui.home

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.SparseIntArray
import android.view.*
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.appbar.AppBarLayout
import com.jess.arms.base.BaseFragment
import com.jess.arms.integration.RepositoryManager
import com.jess.arms.mvp.IPresenter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.shentu.paper.R
import com.shentu.paper.app.GlideApp
import com.shentu.paper.app.event.LikeEvent
import com.shentu.paper.model.entity.Banner
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.mvp.presenter.MainPresenter
import com.shentu.paper.mvp.ui.activity.SearchActivity
import com.shentu.paper.mvp.ui.adapter.HomeBannerAdapter
import com.shentu.paper.mvp.ui.adapter.RecommendNewAdapter
import com.shentu.paper.mvp.ui.adapter.decoration.RandomRecommendDecoration
import com.shentu.paper.mvp.ui.browser.PictureBrowserActivity
import com.shentu.paper.mvp.ui.my.ContentMode
import com.shentu.paper.mvp.ui.widget.CustomPopWindow
import com.shentu.paper.mvp.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_setting_more.*
import kotlinx.android.synthetic.main.fragment_tab_home.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

@AndroidEntryPoint
class TabHomeFragment : BaseFragment<MainPresenter>(), OnRefreshListener, OnLoadMoreListener,
    ViewPager.OnPageChangeListener  {

    @Inject
    lateinit var errorHandler: RxErrorHandler

    @Inject
    lateinit var repositoryManager: RepositoryManager

    private var popWindow: CustomPopWindow? = null
    private var subType = -1//主题分类
    private var typeSparse: SparseIntArray? = null
    private lateinit var recommendAdapter: RecommendNewAdapter
    private var bannerAdapter: HomeBannerAdapter? = null
    private var isLoading: Boolean = false
    private var isLightMode = false
    private var banners: MutableList<Banner> = arrayListOf()
    private var wallpapers: MutableList<Wallpaper> = mutableListOf()
    private var countdown: Boolean = true
    private var bViewPager: ViewPager? = null
    private var needRefresh = false

    private val homeViewModel: HomeViewModel by viewModels()
    private var homeJob: Job? = null


    override fun initView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tab_home, container, false)
    }

    @SuppressLint("RestrictedApi", "ClickableViewAccessibility")
    override fun initData(savedInstanceState: Bundle?) {
        val lp = bgSearch.layoutParams
        lp.height = BarUtils.getStatusBarHeight() + ConvertUtils.dp2px(40.0f)
        bgSearch.layoutParams = lp
        BarUtils.addMarginTopEqualStatusBarHeight(tvSearch)

        //初始化筛选项
        typeSparse = SparseIntArray()
        typeSparse!!.append(R.id.mTvDefault, -1)
        typeSparse!!.append(R.id.mTvWallpaper, 1)
        typeSparse!!.append(R.id.mTvCos, 2)
        typeSparse!!.append(R.id.mTvGirl, 3)

        recommendAdapter = RecommendNewAdapter()
        recommendAdapter.setOnClickListener(object : RecommendNewAdapter.OnClickListener {
            override fun onClick(adapter: RecommendNewAdapter, view: View, position: Int) {
                val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(
                    view, view.width / 2, view.height / 2, 0, 0
                )
                PictureBrowserActivity.open(position, object : PictureBrowserActivity.Callback {
                    override fun getWallpaperList(): MutableList<Wallpaper> {
                        return wallpapers
                    }

                    override fun loadMore(viewPager: ViewPager) {
                        loadData(false)
                        bViewPager = viewPager
                    }

                }, compat, context = mContext)
            }
        })
        recommendAdapter.addLoadStateListener {
            if(it.refresh is LoadState.Error){
                (it.refresh as LoadState.Error).error.message?.let { it1 ->
                    showMessage(
                        it1
                    )
                }
            }
            when(it.append){

            }
        }

//        recommendAdapter.addHeaderView(getRecommendHead(), 0, StaggeredGridLayoutManager.VERTICAL)
        rvHot.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvHot.addItemDecoration(RandomRecommendDecoration(ConvertUtils.dp2px(12.0f)))
        rvHot.adapter = recommendAdapter
//        rvHot.addView(getRecommendHead(), 0)

        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
            //            Timber.e("current:$i total:${appBarLayout.totalScrollRange} p:${i * 1.0f / appBarLayout.totalScrollRange}")
            val percent: Float = abs(i) * 1.0f / appBarLayout.totalScrollRange//向上滚动，增大
            bgSearch.setBackgroundColor(
                ArgbEvaluator().evaluate(
                    percent,
                    Color.TRANSPARENT,
                    Color.WHITE
                ) as Int
            )
            if (percent - 0.5f > 0) {
                activity?.let {
                    isLightMode = true
                    BarUtils.setStatusBarLightMode(it, true)
                }
                val color = Color.parseColor("#666666")
                tvSearch.setTextColor(color)
                tvSearch.supportBackgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#F5F5F5"))
                tvSearch.supportCompoundDrawablesTintList = ColorStateList.valueOf(color)
            } else {
                activity?.let {
                    isLightMode = false
                    BarUtils.setStatusBarLightMode(it, false)
                }
                val color = Color.parseColor("#AAFDFDFD")
                tvSearch.setTextColor(color)
                tvSearch.supportBackgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#4FF5F5F5"))
                tvSearch.supportCompoundDrawablesTintList = ColorStateList.valueOf(color)
            }
            arc1.alpha = max(1.0f - percent * 3, 0f)//双倍速度隐藏与显示，效果更好
        })
        ViewCompat.setTransitionName(tvSearch, getString(R.string.search_transitionName))
        tvSearch.setOnClickListener {
            val compact: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), tvSearch, getString(R.string.search_transitionName)
            )
            startActivity(Intent(mContext, SearchActivity::class.java), compact.toBundle())
        }
        rvHot.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager: StaggeredGridLayoutManager =
                    (recyclerView.layoutManager) as StaggeredGridLayoutManager
                val into = manager.findLastVisibleItemPositions(null)
                val total = manager.itemCount
                Timber.e("total %s , into %s", total, into[0])
                if (total <= 1) {
                    return
                }
                if (total - into[0] < 12 && !isLoading) {
                    isLoading = true
                    loadData(false)
                }
            }
        })
        refreshLayout.autoRefresh()
        homeViewModel.bannerLiveData.observe(this) {
            this.banners = it
            banners.add(Banner(1, "#85A7D6"))
            if (bannerAdapter == null) {
                bannerAdapter = HomeBannerAdapter(banners, mContext)
                bannerPager.offscreenPageLimit = banners.size
                bannerPager.pageMargin = ConvertUtils.dp2px(12.0f)
                bannerPager.adapter = bannerAdapter
                bannerPager.addOnPageChangeListener(this)
                circleIndicator.setViewPager(bannerPager)
                bannerPager.setOnTouchListener { _, event ->
                    if (event?.action == MotionEvent.ACTION_MOVE) {
                        countdown = false
                    }
                    if (event?.action == MotionEvent.ACTION_UP) {
                        countdown = true
                    }
                    false
                }
            } else {
                bannerAdapter?.notifyDataSetChanged()
            }
        }
        homeViewModel.getBanners()
    }

    private fun getRecommendHead(): View {
        val ivHead = ImageView(context)
        val lp = ViewGroup.LayoutParams(-1, -2)
        lp.height = ((ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(12f * 3)) /
                2 * 383 / 900f).toInt()
        ivHead.layoutParams = lp
        ivHead.setOnClickListener {
            startActivity(Intent(context, PaperSummaryActivity::class.java))
        }
        GlideApp.with(this)
            .load(R.drawable.ic_home_newest_entrance)
            .transform(RoundedCorners(ConvertUtils.dp2px(5f)))
            .into(ivHead)
        return ivHead
    }

    override fun onResume() {
        super.onResume()
//        Timber.e("开始计时")
        if (needRefresh) {
            refreshLayout.autoRefresh()
            needRefresh = false
        }
        startCountDown()
    }

    override fun onPause() {
        super.onPause()
        if (disposable != null && !disposable!!.isDisposed) {
//            Timber.e("停止计时")
            disposable!!.dispose()
        }
    }

    fun scrollToTop() {
        if ((rvHot?.layoutManager as StaggeredGridLayoutManager)
                .findFirstVisibleItemPositions(null)[0] > 12
        ) {
            rvHot.scrollToPosition(11)
        }
        rvHot.smoothScrollToPosition(0)
    }

    /**
     * 是否已滚动
     * */
    fun isScrolled(): Boolean {
        return rvHot != null && rvHot.canScrollVertically(-1)
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear) {
            refreshLayout.finishRefresh(500)
        } else {
            refreshLayout.finishLoadMore()
        }
    }

    fun loadData(clear: Boolean) {
        homeJob?.cancel()
        homeJob = lifecycleScope.launch {
            homeViewModel.getRecommends(clear).collectLatest {
                recommendAdapter.submitData(it)
//                if (clear) {
//                    this.wallpapers = wallpapers
//                    recommendAdapter.setNewData(wallpapers)
//                } else {
//                    isLoading = false
//                    recommendAdapter.addData(wallpapers)
//                }
//                if (wallpapers.size > 0) {
//                    bViewPager?.adapter?.notifyDataSetChanged()
//                }
            }
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadData(false)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        loadData(true)
    }

    fun showFilterPop() {
        if (popWindow == null) {
            val contentView = LayoutInflater.from(context)
                .inflate(R.layout.layout_popwindow_filter_hot, null) as ViewGroup
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

    @SuppressLint("Range")
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //Timber.e("position:$position  offest:$positionOffset  pixel:$positionOffsetPixels")
        if (banners.size < 1) {
            return
        }
        try {
            val evaluate = ArgbEvaluator().evaluate(
                positionOffset, Color.parseColor(banners[position].color),
                Color.parseColor(banners[if (position == bannerPager.adapter!!.count - 1) 0 else position + 1].color)
            ) as Int
            arc1.setColorFilter(evaluate)
        } catch (e: Throwable) {
            ToastUtils.showShort(e.message)
        }
    }

    override fun onPageSelected(position: Int) {
//        arc1.setImageResource(Color.parseColor(banners[position].color))
    }

    private var disposable: Disposable? = null

    private fun startCountDown() {
        Observable.interval(6, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { t -> disposable = t }
            .subscribe(object : ErrorHandleSubscriber<Long>(errorHandler) {
                override fun onNext(t: Long) {
//                        Timber.e("首页正在计时...")
                    if (!countdown || bannerAdapter == null) {
                        return
                    }
                    bannerPager.currentItem =
                        if (bannerAdapter!!.count - bannerPager.currentItem < 1) 0
                        else bannerPager.currentItem + 1
                }

                override fun onError(t: Throwable) {
                    Timber.e(t)
                }
            })
    }

    override fun useEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun addLike(event: LikeEvent) {
        recommendAdapter.notifyItemChanged(event.position)
//        recommendAdapter.refreshNotifyItemChanged(event.position)
    }

    @Subscribe
    fun switchContentMode(mode: ContentMode) {
        needRefresh = true
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
