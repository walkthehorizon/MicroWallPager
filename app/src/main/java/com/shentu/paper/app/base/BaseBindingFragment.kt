package com.shentu.paper.app.base//package com.micro.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.micro.mvp.IView
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.page.EmptyCallback
import com.shentu.paper.app.page.ErrorCallback
import com.shentu.paper.app.page.LoadingCallback
import com.shentu.paper.mvp.ui.login.LoginActivity
import inflateBindingWithGeneric
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseBindingFragment<VB : ViewBinding> : Fragment(), IView {

    private var isDataLoaded = false // 数据是否已请求
    lateinit var binding: VB
    protected val loadService: LoadService<*>? by lazy {
        if (getLoadTarget() != null) LoadSir.getDefault()
            .register(getLoadTarget()) { onReload() } else null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflateBindingWithGeneric(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isDataLoaded) {
            isDataLoaded = true
            lazyLoadData()
        }
    }

    // 实现具体的数据请求逻辑
    protected open fun lazyLoadData() {}

    protected open fun getLoadTarget(): View? {
        return null
    }

    protected open fun onReload() {

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

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    protected open fun runAfterLogin(action: () -> Unit) {
        if (!HkUserManager.isLogin) {
            launchActivity(Intent(context, LoginActivity::class.java))
            LoginActivity.action = action
        } else {
            action.invoke()
        }
    }

    protected open fun useEventBus(): Boolean = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}