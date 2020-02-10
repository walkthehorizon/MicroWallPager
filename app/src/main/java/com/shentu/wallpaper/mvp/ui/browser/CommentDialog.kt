package com.shentu.wallpaper.mvp.ui.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.app.utils.RxUtils
import com.shentu.wallpaper.model.api.service.CommentService
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.Comment
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.model.response.CommentPageResponse
import com.shentu.wallpaper.mvp.ui.adapter.CommentAdapter
import com.shentu.wallpaper.mvp.ui.widget.BaseBottomSheetDialog
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_comment.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber


class CommentDialog : BaseBottomSheetDialog() {

    private lateinit var inputDialog: MaterialDialog

    companion object {
        fun newInstance(paperId: Int): CommentDialog {
            val args = Bundle()
            args.putInt("paperId", paperId)
            val dialog = CommentDialog()
            dialog.arguments = args
            return dialog
        }

    }

    private lateinit var loadService: LoadService<Any>
    private var paperId: Int = -1
    private lateinit var adapter: CommentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paperId = arguments!!.getInt("paperId")
        loadService = LoadSir.getDefault().register(smartRefresh) { getPaperComments(paperId, true) }
        smartRefresh.setOnLoadMoreListener {
            getPaperComments(paperId, false)
        }
        rlInput.setOnClickListener {
            showInputDialog()
        }
        adapter = CommentAdapter(mutableListOf())
        rvComment.layoutManager = LinearLayoutManager(context)
        rvComment.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        rvComment.adapter = adapter

        getPaperComments(paperId, true)
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = llComment.layoutParams
        params.height = ScreenUtils.getScreenHeight() - BarUtils.getStatusBarHeight()
        params.width = -1
        llComment.layoutParams = params

        view?.post {
            val behavior: BottomSheetBehavior<*> = ((view?.parent as View).layoutParams
                    as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior<*>
            behavior.skipCollapsed = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun showComments(comments: List<Comment>, clear: Boolean) {
        if (clear) {
            adapter.setNewData(comments)
        } else {
            adapter.addData(comments)
        }
    }

    private fun showInputDialog() {
        context?.let {
            inputDialog = MaterialDialog(it).show {
                input(hint = "说点什么吧", maxLength = 80) { _, text ->
                    addPaperComments(paperId, text.toString())
                }
                positiveButton(text = "提交")
            }
        }
    }

    override fun showContent() {
        loadService.showSuccess()
    }

    override fun showEmpty() {
        loadService.showCallback(EmptyCallback::class.java)
    }

    override fun showError() {
        loadService.showCallback(ErrorCallback::class.java)
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear) {
            smartRefresh.finishRefresh()
        } else {
            smartRefresh.finishLoadMore()
        }
    }

    var offset = MicroService.PAGE_START
    private lateinit var disposable: Disposable

    private fun getPaperComments(paperId: Int, clear: Boolean) {
        offset = if (clear) MicroService.PAGE_START else offset + MicroService.PAGE_LIMIT
        ArmsUtils.obtainAppComponentFromContext(context)
                .repositoryManager()
                .obtainRetrofitService(CommentService::class.java)
                .getPaperComments(paperId, MicroService.PAGE_LIMIT, offset)
                .compose(RxUtils.applySchedulers(this, clear))
                .subscribe(object : ErrorHandleSubscriber<CommentPageResponse>(
                        ArmsUtils.obtainAppComponentFromContext(context).rxErrorHandler()) {
                    override fun onNext(t: CommentPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        showComments(t.data!!.content, clear)
                    }
                })
    }

    private fun addPaperComments(paperId: Int, content: String) {
        ArmsUtils.obtainAppComponentFromContext(context)
                .repositoryManager()
                .obtainRetrofitService(CommentService::class.java)
                .addPaperComments(paperId, content)
                .compose(RxUtils.applyClearSchedulers(this))
                .subscribe(object : ErrorHandleSubscriber<BaseResponse<Comment>>(
                        ArmsUtils.obtainAppComponentFromContext(context).rxErrorHandler()) {
                    override fun onNext(t: BaseResponse<Comment>) {
                        if (!t.isSuccess) {
                            ToastUtils.showShort("评论失败")
                            return
                        }
                        showContent()
                        t.data?.let { adapter.addData(0, it) }
                        inputDialog.dismiss()
                        callback?.commentAdd()
                        ToastUtils.showShort("评论成功")
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::disposable.isInitialized && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    interface Callback {
        fun commentAdd()
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    private var callback: Callback? = null
}