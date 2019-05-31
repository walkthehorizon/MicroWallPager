package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.reflect.TypeToken
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
import kotlinx.android.synthetic.main.activity_search.*

@Route(path = "/activity/search")
class SearchActivity : BaseActivity<SearchPresenter>(), SearchContract.View, TextWatcher {


    private var curKey = ""
    private val hotAdapter: HotAdapter = HotAdapter(ArrayList())
    private lateinit var loadService: LoadService<Any>
    private var keyQueue: LimitQueue<String> = LimitQueue(12)
    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggersearchComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .searchModule(searchModule(this))
                .build()
                .inject(this)
        BarUtils.setStatusBarLightMode(this, true)
        window.statusBarColor = Color.WHITE
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
        val history = SPUtils.getInstance().getString("search_history", "")
        if (history.isNotEmpty()) {
            keyQueue = ArmsUtils.obtainAppComponentFromContext(this)
                    .gson()
                    .fromJson(history, object : TypeToken<LimitQueue<String>>() {}.type)
        }
        showHistory()

        smartRefresh.setOnLoadMoreListener { loadData(false) }
        etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    curKey = etSearch.text.toString()
                    loadData(true)
                    KeyboardUtils.hideSoftInput(etSearch)
                    return true
                }
                return false
            }
        })
        etSearch.addTextChangedListener(this)
        rvData.layoutManager = LinearLayoutManager(this)
        rvData.addItemDecoration(HotPageRvDecoration(12))
        rvData.adapter = hotAdapter
    }

    fun loadData(clear: Boolean) {
        if (TextUtils.isEmpty(curKey)) {
            ToastUtils.showShort("无效的关键字")
            return
        }
        if (clear) {
            keyQueue.offer(curKey, true)
            SPUtils.getInstance().put("search_history",
                    ArmsUtils.obtainAppComponentFromContext(this)
                            .gson()
                            .toJson(keyQueue))
        }
        mPresenter?.searchKey(curKey, clear)
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

    override fun showHistory() {
        loadService.setCallBack(SearchHistoryCallback::class.java) { context, view ->
            val chipGroup = view?.findViewById<ChipGroup>(R.id.chipGroup)
            chipGroup?.removeAllViews()
            val lp = ChipGroup.LayoutParams(-2, ConvertUtils.dp2px(30.0f))
            for (key in keyQueue.queue) {
                val chip = Chip(context)
                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(
                        this, R.color.colorAccent))
                chip.chipStrokeWidth = ConvertUtils.dp2px(1.0f).toFloat()
                chip.chipBackgroundColor = ColorStateList.valueOf(Color.WHITE)
                chip.text = key
                chip.setOnClickListener {
                    curKey = chip.text.toString()
                    etSearch.setText(curKey)
                    etSearch.setSelection(etSearch.length())
                    loadData(true)
                }
                chipGroup?.addView(chip, lp)
            }
        }
        loadService.showCallback(SearchHistoryCallback::class.java)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (TextUtils.isEmpty(s)) {
            showHistory()
        }
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
