package com.shentu.paper.mvp.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.shentu.paper.R
import com.shentu.paper.app.base.BaseBindingActivity
import com.shentu.paper.databinding.ActivitySearchBinding
import com.shentu.paper.model.entity.Subject
import com.shentu.paper.mvp.ui.adapter.HotAdapter
import com.shentu.paper.mvp.ui.adapter.decoration.HotPageRvDecoration
import com.shentu.paper.mvp.ui.browser.PaperBrowserActivity
import com.shentu.paper.mvp.ui.browser.SourceSubject
import com.shentu.paper.mvp.ui.search.SearchHistoryView
import com.shentu.paper.mvp.ui.widget.AbsTextWatcher
import com.shentu.paper.mvp.ui.widget.DefaultToolbar
import com.shentu.paper.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SearchActivity : BaseBindingActivity<ActivitySearchBinding>() {

    private val hotAdapter: HotAdapter = HotAdapter(ArrayList())
    private val searchViewModel by viewModels<SearchViewModel>()

    override fun getLoadTarget(): View {
        return binding.rvData
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
                if(it.data?.count==0){
                    showEmpty()
                    return@observe
                }
                hotAdapter.setNewData(it.data?.content)
                showContent()
            })
        searchViewModel.historyLiveData.observe(this,{
            showLoading()
            if(it.isNullOrEmpty()){
                binding.rvData.visibility = View.GONE
                binding.historyView.show()
            }else{
                binding.rvData.visibility = View.VISIBLE
                binding.historyView.updateKey(it)
            }
        })
        showContent()
    }

    override fun onStop() {
        super.onStop()
        KeyboardUtils.hideSoftInput(etSearch)
        etSearch.clearFocus()
    }
}
