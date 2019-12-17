package com.shentu.wallpaper.app.utils

import com.shentu.wallpaper.BuildConfig
import com.shentu.wallpaper.app.HkApplication
import com.shentu.wallpaper.app.HkUserManager
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
                .setDebuggable(true)
                .builder()
        val credentialProvider = SessionCredentialProvider(HttpRequest.Builder<String>()
                .url(URL(BuildConfig.Sever + "signature"))
                .method("GET")
                .build())
        cosXmlService = CosXmlSimpleService(HkApplication.instance, serviceConfig,credentialProvider)
    }

    companion object {
        val instance = SingleHolder.holder
    }

    private object SingleHolder {
        val holder = CosUtils()
    }

    fun uploadAvatar(srcPath: String, listener: CosXmlResultListener) {
        Timber.e("avatar本地路径：$srcPath")
        val cosPath = "avatar/" + HkUserManager.instance.uid + "_" + HkUserManager.instance.user.phone + ".jpg"
        val putObjectRequest = PutObjectRequest(bucket, cosPath, srcPath)
        // 使用异步回调上传
        cosXmlService.putObjectAsync(putObjectRequest, listener)
    }
}