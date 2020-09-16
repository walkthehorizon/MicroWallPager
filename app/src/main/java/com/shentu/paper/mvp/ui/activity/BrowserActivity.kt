package com.shentu.paper.mvp.ui.activity

import android.content.Context
import android.content.Intent
import android.net.http.SslCertificate
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.*
import androidx.core.content.ContextCompat
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.just.agentweb.*
import com.shentu.paper.R
import com.shentu.paper.app.utils.UIController
import com.shentu.paper.app.webview.AndroidInterface
import com.shentu.paper.app.webview.CustomWebSettingsImpl
import kotlinx.android.synthetic.main.activity_browser.*
import timber.log.Timber


class BrowserActivity : BaseActivity<IPresenter>() {

    private lateinit var agentWeb: AgentWeb
    private var mMiddleWareWebChrome: MiddlewareWebChromeBase = object : MiddlewareWebChromeBase() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            if (consoleMessage?.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                Timber.e(consoleMessage.message())
            }
            return super.onConsoleMessage(consoleMessage)
        }
    }

    private var mMiddleWareWebClient: MiddlewareWebClientBase = object : MiddlewareWebClientBase() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Timber.e("onPageFinished：%s", url)
            agentWeb.webCreator.webView.evaluateJavascript("(function(){"
//                    + "let imgs = document.getElementsByClassName('rich_pages');"
                    + "let imgs = document.getElementsByTagName('img');"
                    + "let pictures = new Array();"
                    + "for(let i=0;i<imgs.length;i++){"
                    + "    console.error(imgs[i].getAttribute('data-src'));"
                    + "    pictures[i] = imgs[i].getAttribute('data-src');"
                    + "}"
                    + "for(let i=0;i<imgs.length;i++){"
                    + "    imgs[i].addEventListener('click',function(event){"
                    + "        window.android.showImages(pictures,i);"
                    + "    });"
                    + "}"
                    + "})()", null)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            Timber.e(error.toString())
        }
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
                .useDefaultIndicator(ContextCompat.getColor(this, R.color.colorAccent), -1)
                .setPermissionInterceptor(null)
                .setWebLayout(null)
                .setAgentWebUIController(UIController(this))
                .interceptUnkownUrl()
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .useMiddlewareWebChrome(mMiddleWareWebChrome)
                .useMiddlewareWebClient(mMiddleWareWebClient)
                .setAgentWebWebSettings(CustomWebSettingsImpl)
                .setMainFrameErrorView(R.layout.layout_default_error, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .addJavascriptInterface("android", AndroidInterface(this))
                .createAgentWeb()
                .ready()
                .go(intent.getStringExtra("url"))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        return if (agentWeb.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        agentWeb.webLifeCycle.onPause()
        super.onPause()

    }

    override fun onResume() {
        agentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb.webLifeCycle.onDestroy()
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
