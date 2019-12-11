package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.app.page.LoadingCallback
import com.shentu.wallpaper.app.page.SearchHistoryCallback
import com.shentu.wallpaper.app.utils.LimitQueue
import com.shentu.wallpaper.di.component.DaggersearchComponent
import com.shentu.wallpaper.di.module.searchModule
import com.shentu.wallpaper.model.entity.Subject
import com.shentu.wallpaper.mvp.contract.SearchContract
import com.shentu.wallpaper.mvp.presenter.SearchPresenter
import com.shentu.wallpaper.mvp.ui.adapter.HotAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.HotPageRvDecoration
import com.shentu.wallpaper.mvp.ui.browser.PictureBrowserActivity
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

        smartRefresh.setOnLoadMoreListener { mPresenter?.loadMore() }
        etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyboardUtils.hideSoftInput(etSearch)
                    return true
                }
                return false
            }
        })
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

        mPresenter?.init()
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        GlobalScope.launch {
            delay(400)
            runOnUiThread {
                etSearch.requestFocus()
            }
        }
    }

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

    override fun showNOMoreData() {
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
            val lp = ChipGroup.LayoutParams(-2, ConvertUtils.dp2px(30.0f))
            for (key in queue.queue) {
                val chip = Chip(context)
                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(
                        this, R.color.colorAccent))
                chip.chipStrokeWidth = ConvertUtils.dp2px(1.0f).toFloat()
                chip.chipBackgroundColor = ColorStateList.valueOf(Color.WHITE)
                chip.text = key
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
        ArmsUtils.snackbarText(message)
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
