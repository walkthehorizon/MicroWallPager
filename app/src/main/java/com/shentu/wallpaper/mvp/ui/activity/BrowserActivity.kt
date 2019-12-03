package com.shentu.wallpaper.mvp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.just.agentweb.*
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.utils.UIController
import kotlinx.android.synthetic.main.activity_browser.*

class BrowserActivity : BaseActivity<IPresenter>() {

    private var agentWeb: AgentWeb? = null
    private var mMiddleWareWebChrome: MiddlewareWebChromeBase = object : MiddlewareWebChromeBase() {

    }
    private var mMiddleWareWebClient: MiddlewareWebClientBase = object : MiddlewareWebClientBase() {

    }

    override fun setupActivityComponent(appComponent: AppComponent) {

    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_browser
    }

    override fun initData(savedInstanceState: Bundle?) {
        buildAgentWeb()
    }

    private fun buildAgentWeb() {
        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(container, ViewGroup.LayoutParams(-1, -1))
                .useDefaultIndicator(ContextCompat.getColor(this,R.color.colorAccent), -1)
                .setPermissionInterceptor(null)
                .setWebLayout(null)
                .setAgentWebUIController(UIController(this))
                .interceptUnkownUrl()
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .useMiddlewareWebChrome(mMiddleWareWebChrome)
                .useMiddlewareWebClient(mMiddleWareWebClient)
                .setAgentWebWebSettings(AgentWebSettingsImpl.getInstance())
                .setMainFrameErrorView(R.layout.layout_default_error, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .createAgentWeb()
                .ready()
                .go(intent.getStringExtra("url"))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        return if (agentWeb != null && agentWeb!!.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        if (agentWeb != null) {
            agentWeb!!.webLifeCycle.onPause()
        }
        super.onPause()

    }

    override fun onResume() {
        if (agentWeb != null) {
            agentWeb!!.webLifeCycle.onResume()
        }
        super.onResume()
    }

    override fun onDestroy() {
        if (agentWeb != null) {
            agentWeb!!.webLifeCycle.onDestroy()
        }
        super.onDestroy()
    }

    companion object {
        fun open(context: Context, url: String) {
            val intent = Intent(context, BrowserActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }
}
