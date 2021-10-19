package com.shentu.paper.mvp.ui.activity

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.shentu.paper.R
import com.shentu.paper.app.base.BaseBindingActivity
import com.shentu.paper.databinding.ActivityMyCollectBinding
import com.shentu.paper.model.body.DelCollectBody
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.mvp.ui.adapter.CollectListAdapter
import com.shentu.paper.mvp.ui.adapter.decoration.CollectListDecoration
import com.shentu.paper.mvp.ui.browser.PaperBrowserActivity
import com.shentu.paper.mvp.ui.browser.SourceCollect
import com.shentu.paper.viewmodels.CollectViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCollectActivity : BaseBindingActivity<ActivityMyCollectBinding>() {

    private val viewModel by viewModels<CollectViewModel>()
    private lateinit var adapter: CollectListAdapter

    private fun showDelDialog() {
        MaterialDialog(this).show {
            title(text = "删除")
            message(text = "确定删除所选收藏么")
            positiveButton(text = "确定") {
                val delList = mutableListOf<Long>()
                for (paper in adapter.data) {
                    if (paper.checked) delList.add(paper.id)
                }
                viewModel.delCollectPaper(DelCollectBody(delList))
            }
            negativeButton(text = "取消")
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        window.exitTransition = Slide(Gravity.START)
        binding.smartRefresh.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                viewModel.loadMyCollects(false)
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                viewModel.loadMyCollects(true)
            }
        })
        binding.rvCollect.layoutManager = GridLayoutManager(this, 3)
        binding.rvCollect.addItemDecoration(CollectListDecoration(ConvertUtils.dp2px(4.0f)))
        adapter = CollectListAdapter(decoration = ConvertUtils.dp2px(16f))
        (binding.rvCollect.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        binding.rvCollect.adapter = adapter
        adapter.setOnItemClickListener { a, view, position ->
            val paper = a.data[position] as Wallpaper
            if ((a as CollectListAdapter).getMode()) {
                paper.checked = !paper.checked
                a.notifyItemChanged(position)
                //检查删除按钮状态
                var enabled = false
                a.data.forEach {
                    if (it.checked) {
                        enabled = true
                        return@forEach
                    }
                }
                binding.tvDelete.isEnabled = enabled
                return@setOnItemClickListener
            }
            ViewCompat.setTransitionName(view, resources.getString(R.string.picture_transitionName))
            val compat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            PaperBrowserActivity.open(this, SourceCollect(adapter.data ,position), compat)
        }
        binding.tvDelete.setOnClickListener {
            showDelDialog()
        }
        binding.tvSelect.setOnClickListener {
            if (binding.tvSelect.text == "全选") {
                binding.tvSelect.text = "取消全选"
            } else {
                binding.tvSelect.text = "全选"
            }
            for (paper in adapter.data) {
                paper.checked = binding.tvSelect.text != "全选"
            }
            adapter.notifyModeChanged(true)
        }
        binding.ivMore.setOnClickListener {
            openDelMode()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.loadMyCollects(true)
        viewModel.liveData.observe(this, {
            if (!it.isSuccess) {
                showError()
                return@observe
            }
            val clear = it.data!!.previous == null
            hideRefresh(clear, it.data.next == null)
            if (clear) {
                adapter.setNewData(it.data.content)
            } else {
                adapter.addData(it.data.content)
            }
            showContent()
        })
        viewModel.delLiveData.observe(this, {
            closeDelMode(true)
            ToastUtils.showShort("删除成功")
        })
    }

    override fun getLoadTarget(): View {
        return binding.smartRefresh
    }

    override fun getRefreshLayout(): SmartRefreshLayout {
        return binding.smartRefresh
    }

    override fun onBackPressed() {
        if (adapter.getMode()) {
            closeDelMode()
            return
        }
        super.onBackPressed()
    }

    private fun openDelMode() {
        binding.tvDelete.visibility = View.VISIBLE
        binding.tvSelect.visibility = View.VISIBLE
        binding.ivMore.visibility = View.GONE
        adapter.notifyModeChanged(true)
    }

    private fun closeDelMode(removeData: Boolean = false) {
        if (removeData) {
            val it: MutableIterator<Wallpaper> = (adapter.data as MutableList).iterator()
            while (it.hasNext()) {
                if (it.next().checked) {
                    it.remove()
                }
            }
        }
        binding.tvSelect.visibility = View.GONE
        binding.tvSelect.text = "全选"
        binding.tvDelete.visibility = View.GONE
        binding.tvDelete.isEnabled = false
        binding.ivMore.visibility = View.VISIBLE
        adapter.notifyModeChanged(false)
    }
}
