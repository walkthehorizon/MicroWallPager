package com.shentu.paper.app.utils

import com.shentu.paper.BuildConfig
import com.shentu.paper.app.AppLifecycleImpl
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.config.Config
import com.tencent.cos.xml.CosXmlServiceConfig
import com.tencent.cos.xml.CosXmlSimpleService
import com.tencent.cos.xml.listener.CosXmlResultListener
import com.tencent.cos.xml.model.`object`.PutObjectRequest
import com.tencent.qcloud.core.auth.SessionCredentialProvider
import com.tencent.qcloud.core.http.HttpRequest
import timber.log.Timber
import java.net.URL


class CosUtils private constructor() {

    private var cosXmlService: CosXmlSimpleService
    private val bucket = "wallpager-1251812446"

    init {
        val serviceConfig = CosXmlServiceConfig.Builder()
                .setRegion("ap-beijing")
                .isHttps(true) // 使用 https 请求, 默认 http 请求
                .setDebuggable(BuildConfig.DEBUG)
                .builder()
        val credentialProvider = SessionCredentialProvider(HttpRequest.Builder<String>()
                .url(URL(Config.appServer.replace("https", "http") + "signature"))
                .method("GET")
                .build())
        cosXmlService = CosXmlSimpleService(AppLifecycleImpl.instance, serviceConfig, credentialProvider)
    }

    companion object {
        val instance = SingleHolder.holder
    }

    private object SingleHolder {
        val holder = CosUtils()
    }

    fun uploadAvatar(srcPath: String, listener: CosXmlResultListener) {
        Timber.e("avatar本地路径：$srcPath")
        val cosPath = "avatar/" + HkUserManager.uid + "/" + System.currentTimeMillis() + ".jpg"
        val putObjectRequest = PutObjectRequest(bucket, cosPath, srcPath)
        // 使用异步回调上传
        cosXmlService.putObjectAsync(putObjectRequest, listener)
    }
}