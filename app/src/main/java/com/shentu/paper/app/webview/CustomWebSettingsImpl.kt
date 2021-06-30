package com.shentu.paper.app.webview

import android.webkit.WebView
import com.just.agentweb.AgentWebSettingsImpl
import com.just.agentweb.IAgentWebSettings

object CustomWebSettingsImpl : AgentWebSettingsImpl() {

    override fun toSetting(webView: WebView?): IAgentWebSettings<*> {
        webView?.let {
            it.settings.mixedContentMode = 0
        }
        return super.toSetting(webView)
    }

}