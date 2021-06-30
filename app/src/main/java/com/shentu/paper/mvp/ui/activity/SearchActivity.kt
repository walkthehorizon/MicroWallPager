package com.shentu.paper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.chip.ChipGroup
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.paper.R
import com.shentu.paper.app.page.EmptyCallback
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.app.page.LoadingCallback
import com.shentu.paper.app.page.SearchHistoryCallback
import com.shentu.paper.app.utils.LimitQueue
import com.shentu.paper.di.component.DaggersearchComponent
import com.shentu.paper.di.module.searchModule
import com.shentu.paper.model.entity.Subject
import com.shentu.paper.mvp.contract.SearchContract
import com.shentu.paper.mvp.presenter.SearchPresenter
import com.shentu.paper.mvp.ui.adapter.HotAdapter
import com.shentu.paper.mvp.ui.adapter.decoration.HotPageRvDecoration
import com.shentu.paper.mvp.ui.browser.PictureBrowserActivity
import com.shentu.paper.mvp.ui.widget.DefaultToolbar
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Route(path = "/activity/search")
class SearchActivity : BaseActivity<SearchPresenter>(), SearchContract.View {


    private val hotAdapter: HotAdapter = HotAdapter(ArrayList())
    private lateinit var loadService: LoadService<Any>

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggersearchComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .searchModule(searchModule(this))
                .build()
                .inject(this)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_search
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadService = LoadSir.Builder()
                .addCallback(EmptyCallback())
                .addCallback(LoadingCallback())
                .addCallback(ErrorCallback())
                .addCallback(SearchHistoryCallback())
                .setDefaultCallback(LoadingCallback::class.java)
                .build()
                .register(smartRefresh)

        toolbar.addOnClickListener(object : DefaultToolbar.OnClickListener() {
            override fun onClickLeftIcon() {
                KeyboardUtils.hideSoftInput(etSearch)
                etSearch.clearFocus()
            }
        })
        smartRefresh.setOnLoadMoreListener { mPresenter?.loadMore() }
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter?.search(s.toString())
            }
        })
        rvData.layoutManager = LinearLayoutManager(this)
        rvData.addItemDecoration(HotPageRvDecoration(12))
        hotAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            val subject = adapter.data[position] as Subject
            val current = when (view.id) {
                R.id.iv_2 -> 1
                R.id.iv_3 -> 2
                else -> 0
            }
            PictureBrowserActivity.open(current = current, subjectId = subject.id, context = this)
        }
        rvData.adapter = hotAdapter

        rvData.post {
            mPresenter?.init()
        }

        GlobalScope.launch {
            delay(500)
            runOnUiThread {
                etSearch.isFocusable = true
                etSearch.isFocusableInTouchMode = true
                etSearch.requestFocus()
                KeyboardUtils.showSoftInput(etSearch)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        KeyboardUtils.hideSoftInput(etSearch)
        etSearch.clearFocus()
    }

//    override fun onEnterAnimationComplete() {
//        super.onEnterAnimationComplete()
//        GlobalScope.launch {
//            delay(500)
//            runOnUiThread {
//                etSearch.isFocusable = true
//                etSearch.isFocusableInTouchMode = true
//                etSearch.requestFocus()
//                KeyboardUtils.showSoftInput(etSearch)
//            }
//        }
//    }

    override fun showContent() {
        loadService.showSuccess()
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear) {
            smartRefresh.finishRefresh()
        } else {
            smartRefresh.finishLoadMore()
        }
    }

    override fun showLoading() {
        loadService.showCallback(LoadingCallback::class.java)
    }

    override fun showEmpty() {
        loadService.showCallback(EmptyCallback::class.java)
    }

    override fun showError() {
        loadService.showCallback(ErrorCallback::class.java)
    }

    override fun showNoMoreData() {
        smartRefresh.finishLoadMoreWithNoMoreData()
    }

    override fun showResults(subjects: MutableList<Subject>, clear: Boolean) {
        if (clear) {
            hotAdapter.setNewData(subjects)
        } else {
            hotAdapter.addData(subjects)
        }
    }

    override fun showHistory(queue: LimitQueue<String>) {
        Timber.e(queue.size().toString())
        loadService.setCallBack(SearchHistoryCallback::class.java) { context, view ->
            val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
            val tvClear = view.findViewById<TextView>(R.id.tvClear)
            chipGroup.removeAllViews()
            val lp = ChipGroup.LayoutParams(-2, ConvertUtils.dp2px(20f))
            chipGroup.chipSpacingVertical = ConvertUtils.dp2px(6f)
            for (key in queue.queue) {
                val chip = TextView(context)
                chip.minWidth = ConvertUtils.dp2px(40f)
                chip.setTextColor(ContextCompat.getColor(context,R.color.colorNormalText))
                val padding = ConvertUtils.dp2px(8.0f)
                chip.setPadding(padding,0,padding,0)
                chip.setBackgroundResource(R.drawable.bg_search_history_item)
//                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(
//                        this, R.color.colorAccent))
//                chip.chipStrokeWidth = ConvertUtils.dp2px(1.0f).toFloat()
//                chip.chipBackgroundColor = ColorStateList.valueOf(Color.WHITE)
                chip.text = key
                chip.gravity = Gravity.CENTER
                chip.setOnClickListener {
                    val keyStr = chip.text.toString()
                    etSearch.setText(keyStr)
                    etSearch.setSelection(etSearch.text.length)
                    etSearch.requestFocus()
                    mPresenter?.search(keyStr)
                }
                chipGroup.addView(chip, lp)
            }
            if (chipGroup.childCount > 0) {
                tvClear.visibility = View.VISIBLE
                tvClear.setOnClickListener {
                    chipGroup.removeAllViews()
                    mPresenter?.clearHistory()
                    tvClear.visibility = View.GONE
                }
            }
        }
        loadService.showCallback(SearchHistoryCallback::class.java)
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

        finishAfterTransition()
    }

    companion object {
        fun open(compat: ActivityOptionsCompat) {
            ARouter.getInstance()
                    .build("/activity/search")
                    .withOptionsCompat(compat)
                    .navigation()
        }
    }
}
