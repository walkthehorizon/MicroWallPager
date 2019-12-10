package com.shentu.wallpaper.app

import android.content.Context
import android.net.ParseException
import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.JsonParseException
import io.reactivex.exceptions.CompositeException
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener
import org.json.JSONException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * ================================================
 * 展示 [ResponseErrorListener] 的用法
 * ================================================
 */
class ResponseErrorListenerImpl : ResponseErrorListener {
    override fun handleResponseError(context: Context, t: Throwable) {
        Timber.e(t)
        //这里不光是只能打印错误,还可以根据不同的错误作出不同的逻辑处理
        var msg = ""
        if (t is UnknownHostException) {
            msg = "网络连接不可用"
        } else if (t is SocketTimeoutException) {
            msg = "请求网络超时"
        } else if (t is ConnectException) {
            msg = "服务器连接异常"
        } else if (t is HttpException) {
            msg = convertStatusCode(t)
        } else if (t is JsonParseException || t is ParseException || t is JSONException) {
            msg = "数据解析错误"
        } else if (t is CompositeException) {
            for (e in t.exceptions) {
                handleResponseError(context, e)
            }
        }
        if (!TextUtils.isEmpty(msg)) {
            ToastUtils.showShort(msg)
        }
    }

    private fun convertStatusCode(httpException: HttpException): String {
        return when {
            httpException.code() == 500 -> {
                "服务器发生错误"
            }
            httpException.code() == 404 -> {
                "请求地址不存在"
            }
            httpException.code() == 403 -> {
                "请求被服务器拒绝"
            }
            httpException.code() == 307 -> {
                "请求被重定向到其他页面"
            }
            else -> {
                httpException.message()
            }
        }
    }
}