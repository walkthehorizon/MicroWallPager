package com.shentu.paper.mvp.ui.browser

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.shentu.paper.R
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.base.BaseBindingFragment
import com.shentu.paper.app.utils.ShareUtils
import com.shentu.paper.databinding.FragmentPictureBrowserBinding
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.mvp.ui.activity.SubjectDetailActivity
import com.shentu.paper.mvp.ui.adapter.PaperBrowserVpAdapter
import com.shentu.paper.mvp.ui.fragment.PictureFragment
import com.shentu.paper.mvp.ui.login.LoginActivity
import com.shentu.paper.viewmodels.PictureBrowserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_picture_browser.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class PaperBrowserFragment : BaseBindingFragment<FragmentPictureBrowserBinding>(),PictureFragment.Callback {

    private val pictureViewModel: PictureBrowserViewModel by viewModels()
    private lateinit var curPaper: Wallpaper
    private lateinit var vpAdapter: PaperBrowserVpAdapter
    private var allPapers: MutableList<Wallpaper> = mutableListOf()
    private lateinit var popupMenu: PopupMenu

    val source: PaperSource by lazy {
        arguments?.getSerializable(KEY_PAPER_SOURCE) as PaperSource
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pictureViewModel.liveDataShare.observe(viewLifecycleOwner) {
            ShareUtils.showShare(requireContext(), it)
        }
        pictureViewModel.liveDataCollect.observe(viewLifecycleOwner) {
            showLikeStatus(it)
        }
    }

    private fun initView() {
        binding.apply {
            ivShare.setOnClickListener {
                pictureViewModel.loadPaperShare(curPaper.id)
            }
            viewPager.apply {
                offscreenPageLimit = 4
                orientation = ViewPager2.ORIENTATION_VERTICAL
                vpAdapter = PaperBrowserVpAdapter(this@PaperBrowserFragment, allPapers)
                adapter = vpAdapter
            }
            viewPager.registerOnPageChangeCallback(OnPageChangeCallback())
            mbLoadOrigin.setOnClickListener {
                getCurrentPictureFragment()?.loadPicture(Behavior.LOAD_ORIGIN)
            }
            ivDownload.setOnClickListener {
                runAfterLogin {
                    requestWritePermission()
                }
            }
            ivMore.setOnClickListener {
                showMenu()
            }
            tvLike.text = curPaper.collectNum.toString()
            tvLike.setOnClickListener {
                if (!HkUserManager.isLogin) {
                    launchActivity(Intent(requireContext(), LoginActivity::class.java))
                    return@setOnClickListener
                }
                tvLike.isClickable = false
                pictureViewModel.modifyPaperCollect(curPaper.id)
            }

            ivShare.setOnClickListener {
                pictureViewModel.loadPaperShare(curPaper.id)
            }

            tvComment.text = curPaper.commentNum.toString()
            tvComment.setOnClickListener {
                showCommentDialog()
            }
        }
    }

    private fun showCommentDialog() {
        val commentDialog = CommentDialog.newInstance(curPaper.id)
        commentDialog.setCallback(object : CommentDialog.Callback {
            override fun commentAdd() {
                curPaper.commentNum += 1
                tvComment.text = curPaper.commentNum.toString()
            }
        })
        commentDialog.show(childFragmentManager, null)
    }

    @AfterPermissionGranted(999)
    private fun requestWritePermission() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            getCurrentPictureFragment()?.loadPicture(Behavior.ONLY_DOWNLOAD_ORIGIN)
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, "下载需要授予存储权限",
                999, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private fun showMenu() {
        if (!this::popupMenu.isInitialized) {
            popupMenu = PopupMenu(requireContext(), ivMore, Gravity.BOTTOM)
            popupMenu.menuInflater.inflate(R.menu.menu_picture_detail, popupMenu.menu)
            if (curPaper.subjectId == -1) {
                popupMenu.menu.findItem(R.id.itSubject).isVisible = false
            }
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.itSetPaper -> getCurrentPictureFragment()?.loadPicture(Behavior.SET_WALLPAPER)
                    R.id.itSubject -> SubjectDetailActivity.open(
                        curPaper.subjectId,
                        requireContext()
                    )
                }
                true
            }
        }
        popupMenu.show()
    }

    override fun lazyLoadData() {
        var position = 0
        when (source) {
            is SourceRecommend -> {
                val sc = source as SourceRecommend
                allPapers.addAll(sc.papers)
                position = sc.curPosition
                curPaper = allPapers[position]
                initView()
                pictureViewModel.liveData.observe(viewLifecycleOwner) {
                    allPapers.addAll(it)
                    vpAdapter.notifyItemRangeInserted(allPapers.size, it.size)
                }
            }
            is SourcePaper -> {
                position = 0
                pictureViewModel.liveDataPaper.observe(viewLifecycleOwner) {
                    curPaper = it
                    initView()
                }
                pictureViewModel.loadPaperDetail((source as SourcePaper).paperId)
            }
            is SourceSubject -> {
                position = (source as SourceSubject).curPosition
                pictureViewModel.liveDataSubject.observe(viewLifecycleOwner) {
                    curPaper = it[(source as SourceRecommend).curPosition]
                    initView()
                }
                pictureViewModel.loadSubjectAllPapers((source as SourceSubject).subjectId)
            }
            else -> throw IllegalArgumentException("找不到合适的PictureBrowser路由！")
        }
        binding.viewPager.setCurrentItem(position,false)
    }

    private fun showLikeStatus(collected: Boolean) {
        curPaper.collected = collected
        curPaper.collectNum = curPaper.collectNum + if (collected) 1 else -1
        tvLike.text = curPaper.collectNum.toString()
        tvLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            if (collected) R.drawable.ic_favorite_black_24dp else R.drawable.ic_favorite_border_black_24dp,
            0,
            0
        )
    }

    @SuppressLint("SetTextI18n")
    private fun refreshLikeState(position: Int) {
        curPaper = allPapers[position]
        binding.tvOrder.text = "${position + 1}/${allPapers.size}"
        tvLike.text = curPaper.collectNum.toString()
        tvLike.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, if (curPaper.collected) R.drawable.ic_favorite_black_24dp else
                R.drawable.ic_favorite_border_black_24dp, 0, 0
        )
        tvLike.isClickable = !curPaper.collected
    }

    override fun onSwitchNavigation() {
        if (rl_head.visibility == View.VISIBLE) {
            rl_head.visibility = View.GONE
            rl_bottom.visibility = View.GONE
            llRight.visibility = View.GONE
        } else {
            rl_head.visibility = View.VISIBLE
            rl_bottom.visibility = View.VISIBLE
            llRight.visibility = View.VISIBLE
        }
    }

    private fun getCurrentPictureFragment(): PictureFragment? {
        return vpAdapter.getFragment(viewPager.currentItem) as PictureFragment?
    }

    companion object {
        const val KEY_PAPER_SOURCE = "KEY_PAPER_SOURCE"

        fun newInstance(source: PaperSource): PaperBrowserFragment {
            val args = Bundle()
            args.putSerializable(KEY_PAPER_SOURCE, source)
            val fragment = PaperBrowserFragment()
            fragment.arguments = args
            return fragment
        }
    }

    inner class OnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        @SuppressLint("SetTextI18n")
        override fun onPageSelected(position: Int) {
            refreshLikeState(position)
            if (source is SourceRecommend) {
                if (position == allPapers.size - 1) {
                    pictureViewModel.loadRecommendPapers(allPapers.size)
                }
            }
        }
    }
}