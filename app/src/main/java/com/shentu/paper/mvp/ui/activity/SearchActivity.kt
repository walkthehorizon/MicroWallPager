package com.shentu.paper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.reflect.TypeToken
import com.micro.utils.ArmsUtils
import com.kingja.loadsir.core.LoadSir
import com.shentu.paper.R
import com.shentu.paper.app.base.BaseBindingActivity
import com.shentu.paper.app.page.EmptyCallback
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.app.page.LoadingCallback
import com.shentu.paper.app.page.SearchHistoryCallback
import com.shentu.paper.app.utils.LimitQueue
import com.shentu.paper.databinding.ActivitySearchBinding
import com.shentu.paper.model.entity.Subject
import com.shentu.paper.model.response.SubjectPageResponse
import com.shentu.paper.mvp.contract.SearchContract
import com.shentu.paper.mvp.ui.adapter.HotAdapter
import com.shentu.paper.mvp.ui.adapter.decoration.HotPageRvDecoration
import com.shentu.paper.mvp.ui.browser.PaperBrowserActivity
import com.shentu.paper.mvp.ui.browser.SourceSubject
import com.shentu.paper.mvp.ui.search.SearchHistoryView
import com.shentu.paper.mvp.ui.widget.AbsTextWatcher
import com.shentu.paper.mvp.ui.widget.DefaultToolbar
import com.shentu.paper.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.delay
import timber.log.Timber

@AndroidEntryPoint
class SearchActivity : BaseBindingActivity<ActivitySearchBinding>() {

    private val hotAdapter: HotAdapter = HotAdapter(ArrayList())
    private val searchViewModel by viewModels<SearchViewModel>()

    override fun getLoadTarget(): View {
        return binding.smartRefresh
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.addOnClickListener(object : DefaultToolbar.OnClickListener() {
            override fun onClickLeftIcon() {
                KeyboardUtils.hideSoftInput(etSearch)
                etSearch.clearFocus()
            }
        })
        etSearch.addTextChangedListener(object : AbsTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchViewModel.search(s.toString())
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
            PaperBrowserActivity.open(this,SourceSubject(current,subject.id))
        }
        rvData.adapter = hotAdapter

        lifecycleScope.launchWhenCreated {
            delay(500)
            runOnUiThread {
                etSearch.isFocusable = true
                etSearch.isFocusableInTouchMode = true
                etSearch.requestFocus()
                KeyboardUtils.showSoftInput(etSearch)
            }
        }
        binding.historyView.setCallback(object : SearchHistoryView.Callback {
            override fun selectKey(key: String) {
                etSearch.setText(key)
                etSearch.setSelection(etSearch.text.length)
                etSearch.requestFocus()
                searchViewModel.search(key)
            }
        })
        searchViewModel.liveData.observe(this,
            {
                if(!it.isSuccess){
                    showError()
                    return@observe
                }
                hotAdapter.setNewData(it.data?.content)
                binding.smartRefresh.setNoMoreData(true)
            })
        searchViewModel.historyLiveData.observe(this,{
            if(it.isNullOrEmpty()){
                binding.rvData.visibility = View.GONE
                binding.historyView.show()
            }else{
                binding.rvData.visibility = View.VISIBLE
                binding.historyView.updateKey(it)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        KeyboardUtils.hideSoftInput(etSearch)
        etSearch.clearFocus()
    }

    override fun showContent() {
        loadService?.showSuccess()
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear) {
            smartRefresh.finishRefresh()
        } else {
            smartRefresh.finishLoadMore()
        }
    }

    override fun showLoading() {
        loadService?.showCallback(LoadingCallback::class.java)
    }

    override fun showEmpty() {
        loadService?.showCallback(EmptyCallback::class.java)
    }

    override fun showError() {
        loadService?.showCallback(ErrorCallback::class.java)
    }

    override fun showNoMoreData() {
        smartRefresh.finishLoadMoreWithNoMoreData()
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
}
