package com.shentu.wallpaper.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
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
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.MyCollectContract
import com.shentu.wallpaper.mvp.presenter.MyCollectPresenter
import com.shentu.wallpaper.mvp.ui.adapter.CollectListAdapter
import com.shentu.wallpaper.mvp.ui.adapter.decoration.CollectListDecoration
import kotlinx.android.synthetic.main.activity_my_collect.*

@Route(path = "/activity/my/collect/")
class MyCollectActivity : BaseActivity<MyCollectPresenter>(), MyCollectContract.View {

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
        rvCollect.adapter = adapter
        adapter.setOnItemClickListener { _, view, position ->
            ViewCompat.setTransitionName(view, resources.getString(R.string.picture_transitionName))
            val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            PictureBrowserActivity.open(this, this.wallpapers, position, compat)
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


    override fun showLoading() {

    }

    override fun hideLoading() {

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
}
