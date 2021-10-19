package com.shentu.paper.app.base//package com.micro.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.micro.mvp.IView
import com.micro.utils.ArmsUtils
import com.micro.utils.Preconditions
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shentu.paper.app.page.EmptyCallback
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.app.page.LoadingCallback
import com.shentu.paper.mvp.ui.widget.progress.DefaultLoadCallback
import inflateBindingWithGeneric
import kotlinx.android.synthetic.main.activity_my_collect.*
import org.greenrobot.eventbus.EventBus

abstract class BaseBindingActivity<VB : ViewBinding> : AppCompatActivity(), IView {
    protected lateinit var binding: VB
    private var loadService: LoadService<Any>? = null
    private val smartRefresh: SmartRefreshLayout? by lazy {
        getRefreshLayout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        binding = inflateBindingWithGeneric(layoutInflater)
        setContentView(binding.root)
        if (getLoadTarget() != null) {
            loadService = LoadSir.getDefault().register(getLoadTarget()) { onReload() }
        }
        initView(savedInstanceState)
        initData(savedInstanceState)
    }

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun initData(savedInstanceState: Bundle?)

    open fun getLoadTarget(): View? {
        return null
    }

    open fun onReload() {
        showContent()
        smartRefresh?.autoRefresh()
    }

    open fun getRefreshLayout(): SmartRefreshLayout? {
        return null
    }

    override fun showContent() {
        loadService?.showSuccess()
    }

    override fun showEmpty() {
        loadService?.showCallback(EmptyCallback::class.java)
    }

    override fun showError() {
        loadService?.showCallback(ErrorCallback::class.java)
    }

    override fun showLoading() {
        loadService?.showCallback(LoadingCallback::class.java)
    }

    override fun hideRefresh(clear: Boolean) {
        if (clear) {
            smartRefresh?.finishRefresh()
        } else {
            smartRefresh?.finishLoadMore()
        }
    }

    override fun hideRefresh(clear: Boolean, noMoreData: Boolean) {
        hideRefresh(clear)
        smartRefresh?.setNoMoreData(noMoreData)
    }

    override fun showMessage(message: String) {
        Preconditions.checkNotNull(message)
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        Preconditions.checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    protected fun useEventBus(): Boolean = false
}