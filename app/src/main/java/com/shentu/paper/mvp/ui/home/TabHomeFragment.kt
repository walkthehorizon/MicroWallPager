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
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.flatMap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.shentu.paper.R
import com.shentu.paper.app.GlideApp
import com.shentu.paper.app.base.BaseBindingFragment
import com.shentu.paper.app.event.LikeEvent
import com.shentu.paper.app.ktCountDown
import com.shentu.paper.app.ktInterval
import com.shentu.paper.databinding.FragmentTabHomeBinding
import com.shentu.paper.model.entity.Banner
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.mvp.ui.activity.SearchActivity
import com.shentu.paper.mvp.ui.adapter.HomeBannerAdapter
import com.shentu.paper.mvp.ui.adapter.RecommendNewAdapter
import com.shentu.paper.mvp.ui.adapter.decoration.RandomRecommendDecoration
import com.shentu.paper.mvp.ui.browser.PictureBrowserActivity
import com.shentu.paper.mvp.ui.my.ContentMode
import com.shentu.paper.mvp.ui.widget.CustomPopWindow
import com.shentu.paper.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_setting_more.*
import kotlinx.android.synthetic.main.fragment_tab_home.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
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
class TabHomeFragment : BaseBindingFragment<FragmentTabHomeBinding>(), OnRefreshListener,
    OnLoadMoreListener,
    ViewPager.OnPageChangeListener {

    private lateinit var recommendAdapter: RecommendNewAdapter
    private var bannerAdapter: HomeBannerAdapter? = null
    private var isLightMode = false
    private var banners: MutableList<Banner> = arrayListOf()
    private var wallpapers: MutableList<Wallpaper> = mutableListOf()
    private var countdown: Boolean = true
    private var bViewPager: ViewPager? = null
    private var needRefresh = false

    private val homeViewModel: HomeViewModel by viewModels()

    override fun lazyLoadData() {
        binding.bgSearch.apply {
            layoutParams.height = BarUtils.getStatusBarHeight() + ConvertUtils.dp2px(40.0f)
            layoutParams = layoutParams
        }

        BarUtils.addMarginTopEqualStatusBarHeight(tvSearch)

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

                }, compat, context = requireContext())
            }
        })
        recommendAdapter.addLoadStateListener {
            Timber.e(it.toString())
            if (it.refresh is LoadState.Error || it.append is LoadState.Error) {
                (it.refresh as LoadState.Error).error.message?.let { it1 ->
                    showMessage(it1)
                    binding.refreshLayout.finishRefresh(false)
                }
                (it.append as LoadState.Error).error.message?.let { it1 ->
                    showMessage(it1)
                    binding.refreshLayout.finishLoadMore(false)
                }
            }
            if (it.refresh is LoadState.NotLoading) {
                binding.refreshLayout.finishRefresh()
            }
            if (it.append is LoadState.NotLoading) {
                binding.refreshLayout.finishLoadMore()
            }
        }

        rvHot.layoutManager = GridLayoutManager(requireContext(), 2)
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
                binding.tvSearch.apply {
                    val color = Color.parseColor("#666666")
                    setTextColor(color)
                    ViewCompat.setBackgroundTintList(
                        this,
                        ColorStateList.valueOf(Color.parseColor("#F5F5F5"))
                    )
                    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(color))
                }
            } else {
                activity?.let {
                    isLightMode = false
                    BarUtils.setStatusBarLightMode(it, false)
                }
                binding.tvSearch.apply {
                    val color = Color.parseColor("#AAFDFDFD")
                    setTextColor(color)
                    ViewCompat.setBackgroundTintList(
                        this,
                        ColorStateList.valueOf(Color.parseColor("#4FF5F5F5"))
                    )
                    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(color))
                }
            }
            arc1.alpha = max(1.0f - percent * 3, 0f)//双倍速度隐藏与显示，效果更好
        })
        ViewCompat.setTransitionName(tvSearch, getString(R.string.search_transitionName))
        tvSearch.setOnClickListener {
            val compact: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(), tvSearch, getString(R.string.search_transitionName)
            )
            startActivity(Intent(requireContext(), SearchActivity::class.java), compact.toBundle())
        }
        refreshLayout.autoRefresh()
        homeViewModel.bannerLiveData.observe(viewLifecycleOwner) {
            this.banners = it
            banners.add(Banner(1, "#85A7D6"))
            if (bannerAdapter == null) {
                bannerAdapter = HomeBannerAdapter(banners, requireContext())
                bannerPager.offscreenPageLimit = banners.size
                bannerPager.pageMargin = ConvertUtils.dp2px(12.0f)
                bannerPager.adapter = bannerAdapter
                bannerPager.addOnPageChangeListener(this)
                circleIndicator.setViewPager(bannerPager)
                bannerPager.setOnTouchListener { _, event ->
                    if (event?.action == MotionEvent.ACTION_DOWN) {
                        countdown = false
                    }
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
        lifecycleScope.launchWhenStarted {
            ToastUtils.showShort("onSTart")
        }
        showContent()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        if ((rvHot?.layoutManager as GridLayoutManager)
                .findFirstVisibleItemPosition() > 12
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
        homeViewModel.viewModelScope.launch {
            homeViewModel.getRecommends(clear).collectLatest {
                recommendAdapter.submitData(it)
            }
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadData(false)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        loadData(true)
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
        ktInterval(6, timeUnit = TimeUnit.SECONDS, onTick = {
            if (!countdown || bannerAdapter == null) {
                return@ktInterval
            }
            bannerPager.currentItem =
                if (bannerAdapter!!.count - bannerPager.currentItem < 1) 0
                else bannerPager.currentItem + 1
        }, scope = lifecycleScope)
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
