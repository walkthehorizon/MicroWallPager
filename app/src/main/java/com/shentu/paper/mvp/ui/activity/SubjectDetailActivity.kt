package com.shentu.paper.mvp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.shentu.paper.R
import com.shentu.paper.app.GlideApp
import com.shentu.paper.app.base.BaseBindingActivity
import com.shentu.paper.databinding.ActivitySubjectDetailBinding
import com.shentu.paper.model.entity.Banner
import com.shentu.paper.mvp.ui.adapter.SubjectDetailAdapter
import com.shentu.paper.mvp.ui.browser.PaperBrowserActivity
import com.shentu.paper.mvp.ui.browser.SourceSubject
import com.shentu.paper.viewmodels.SubjectViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectDetailActivity : BaseBindingActivity<ActivitySubjectDetailBinding>() {
    private lateinit var adapter: SubjectDetailAdapter

    private val type by lazy {
        intent.getIntExtra("type", -1)
    }
    private val subjectId by lazy {
        intent.getIntExtra("subjectId", -1)
    }

    private val banner: Banner by lazy {
        intent.getSerializableExtra("banner") as Banner
    }
    private val viewModel by viewModels<SubjectViewModel>()

    override fun onReload() {
        showLoading()
        loadData()
    }

    override fun getLoadTarget(): View {
        return binding.rvData
    }

    override fun initView(savedInstanceState: Bundle?) {
        if (type == -1) {
            showError()
            return
        }
        adapter = SubjectDetailAdapter()
        adapter.setOnItemClickListener { _, view, position ->
            val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(
                view, view.width / 2, view.height / 2, 0, 0
            )
            PaperBrowserActivity.open(
                this,
                SourceSubject(position, adapter.data[position].subjectId),
                compat
            )
        }
        binding.rvData.apply {
            layoutManager = GridLayoutManager(this@SubjectDetailActivity, 2)
            setHasFixedSize(true)
        }
        binding.rvData.adapter = adapter
    }

    override fun initData(savedInstanceState: Bundle?) {
        loadData()
        viewModel.papersLiveData.observe(this, {
            if (!it.isSuccess) {
                showError()
                return@observe
            }
            if (it.data?.count == 0) {
                showEmpty()
                return@observe
            }
            adapter.setNewData(it.data!!.content)
            showContent()
        })
        viewModel.subjectDetailLiveData.observe(this, {
            if (!it.isSuccess) {
                showMessage(it.msg)
                return@observe
            }
            binding.toolbar.setTitle(it.data!!.name)
            binding.tvDesc.loadData(it.data.description, "text/html", "utf-8")
        })
    }

    private fun loadData() {
        if (type == 1) {
            viewModel.loadSubjectData(subjectId)
        } else {
            binding.toolbar.setTitle(banner.title)
            viewModel.loaBannerData(banner.id)
            binding.ivCover.visibility = VISIBLE
            GlideApp.with(this)
                .load(banner.imageUrl)
                .error(R.drawable.ic_twotone_broken_image_24)
                .into(binding.ivCover)
        }
    }

    companion object {

        fun open(subjectId: Int = -1, context: Context) {
            val intent = Intent(context, SubjectDetailActivity::class.java)
            intent.putExtra("subjectId", subjectId)
            intent.putExtra("type", 1)
            context.startActivity(intent)
        }

        fun open(banner: Banner, context: Context) {
            val intent = Intent(context, SubjectDetailActivity::class.java)
            intent.putExtra("banner", banner)
            intent.putExtra("type", 2)
        }
    }
}
