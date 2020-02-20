package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.page.EmptyCallback
import com.shentu.wallpaper.app.page.ErrorCallback
import com.shentu.wallpaper.di.component.DaggerMyCollectComponent
import com.shentu.wallpaper.di.module.MyCollectModule
import com.shentu.wallpaper.model.body.DelCollectBody
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.MyCollectContract
import com.shentu.wallpaper.mvp.presenter.MyCollectPresenter
import com.shentu.wallpaper.mvp.ui.adapter.CollectListAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.CollectListDecoration
import com.shentu.wallpaper.mvp.ui.browser.PictureBrowserActivity
import kotlinx.android.synthetic.main.activity_my_collect.*

@Route(path = "/activity/my/collect/")
class MyCollectActivity : BaseActivity<MyCollectPresenter>(), MyCollectContract.View {

    private lateinit var loadingDialog: MaterialDialog

    override fun showDelDialog() {
        MaterialDialog(this).show {
            title(text = "删除")
            message(text = "确定删除所选收藏么")
            positiveButton(text = "确定") {
                val delList = mutableListOf<Int>()
                for (paper in adapter.data) {
                    if (paper.checked) delList.add(paper.id)
                }
                mPresenter?.delCollects(DelCollectBody(delList))
            }
            negativeButton(text = "取消")
        }
    }

    private var wallpapers: List<Wallpaper> = ArrayList()
    private lateinit var adapter: CollectListAdapter
    private lateinit var loadService: LoadService<Any>

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerMyCollectComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myCollectModule(MyCollectModule(this))
                .build()
                .inject(this)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_my_collect
    }

    override fun initData(savedInstanceState: Bundle?) {
        window.exitTransition = Slide(GravityCompat.START)
        loadService = LoadSir.getDefault().register(this) {
            showContent()
            smartRefresh.autoRefresh()
        }
        smartRefresh.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mPresenter?.getMyCollects(false)
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                mPresenter?.getMyCollects(true)
            }

        })
        rvCollect.layoutManager = GridLayoutManager(this, 3)
        rvCollect.addItemDecoration(CollectListDecoration(ConvertUtils.dp2px(4.0f)))
        adapter = CollectListAdapter(wallpapers, ConvertUtils.dp2px(16f))
        (rvCollect.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        rvCollect.adapter = adapter
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
                tvDelete.isEnabled = enabled
                return@setOnItemClickListener
            }
            ViewCompat.setTransitionName(view, resources.getString(R.string.picture_transitionName))
            val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            PictureBrowserActivity.open(position, object : PictureBrowserActivity.Callback {
                override fun getWallpaperList(): List<Wallpaper> {
                    return adapter.data
                }

                override fun loadMore() {
                    mPresenter?.getMyCollects(true)
                }

            }, compat, context = this)
        }
        tvDelete.setOnClickListener {
            showDelDialog()
        }
        tvSelect.setOnClickListener {
            if (tvSelect.text == "全选") {
                tvSelect.text = "取消全选"
            } else {
                tvSelect.text = "全选"
            }
            for (paper in adapter.data) {
                paper.checked = tvSelect.text != "全选"
            }
            adapter.notifyModeChanged(true)
        }
        ivMore.setOnClickListener {
            openDelMode()
        }
        mPresenter?.getMyCollects(true)
    }

    override fun showCollects(wallpapers: List<Wallpaper>, clear: Boolean) {
        if (clear) {
            this.wallpapers = wallpapers
            adapter.setNewData(wallpapers)
        } else {
            adapter.addData(wallpapers)
        }
    }

    override fun showProgress() {
        loadingDialog = MaterialDialog(this).show {
            customView(R.layout.dialog_loading)
            maxWidth(literal = ConvertUtils.dp2px(120f))
        }
    }

    override fun hideProgress() {
        loadingDialog.dismiss()
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear) {
            smartRefresh.finishRefresh()
        } else {
            smartRefresh.finishLoadMore()
        }
    }

    override fun showContent() {
        loadService.showSuccess()
    }

    override fun showError() {
        loadService.showCallback(ErrorCallback::class.java)
    }

    override fun showEmpty() {
        loadService.showCallback(EmptyCallback::class.java)
    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }

    override fun onBackPressed() {
        if (adapter.getMode()) {
            closeDelMode()
            return
        }
        super.onBackPressed()
    }

    override fun openDelMode() {
        tvDelete.visibility = View.VISIBLE
        tvSelect.visibility = View.VISIBLE
        ivMore.visibility = View.GONE
        adapter.notifyModeChanged(true)
    }

    override fun closeDelMode(removeData: Boolean) {
        if (removeData) {
            val it: MutableIterator<Wallpaper> = (adapter.data as MutableList).iterator()
            while (it.hasNext()) {
                if (it.next().checked) {
                    it.remove()
                }
            }
        }
        tvSelect.visibility = View.GONE
        tvSelect.text = "全选"
        tvDelete.visibility = View.GONE
        tvDelete.isEnabled = false
        ivMore.visibility = View.VISIBLE
        adapter.notifyModeChanged(false)
    }
}
