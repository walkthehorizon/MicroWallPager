package com.shentu.paper.app

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.GsonBuilder
import com.micro.base.delegate.AppLifecycles
import com.micro.di.module.GlobalConfigModule
import com.micro.http.log.RequestInterceptor
import com.micro.integration.ConfigModule
import com.shentu.paper.BuildConfig
import com.shentu.paper.app.config.Config
import okhttp3.*
import retrofit2.Retrofit
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * ================================================
 * App 的全局配置信息在此配置, 需要将此实现类声明到 AndroidManifest 中
 * ConfigModule 的实现类可以有无数多个, 在 Application 中只是注册回调, 并不会影响性能 (多个 ConfigModule 在多 Module 环境下尤为受用)
 * 不过要注意 ConfigModule 接口的实现类对象是通过反射生成的, 这里会有些性能损耗
 *
 * @see com.micro.arms.base.delegate.AppDelegate
 *
 * @see com.micro.arms.integration.ManifestParser
 * ================================================
 */
class GlobalConfiguration : ConfigModule {
    //cookie存储
    private var cookieStore: ConcurrentHashMap<String, List<Cookie>> = ConcurrentHashMap()
    private var globalHttpHandler: GlobalHttpHandlerImpl? = null

    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
        val cachePath = PathUtils.getExternalAppCachePath()
        val cacheFile = File(cachePath)
        cookieStore = ConcurrentHashMap()
        globalHttpHandler = GlobalHttpHandlerImpl(context)
        if (!BuildConfig.DEBUG) { //Release 时,让框架不再打印 Http 请求和响应的信息
            builder.printHttpLogLevel(RequestInterceptor.Level.NONE)
        }
        builder.baseurl(Config.appServer)
                .cacheFile(cacheFile)
                // 这里提供一个全局处理 Http 请求和响应结果的处理类,可以比客户端提前一步拿到服务器返回的结果,可以做一些操作,比如token超时,重新获取
                .globalHttpHandler(globalHttpHandler) // 用来处理 rxjava 中发生的所有错误,rxjava 中发生的每个错误都会回调此接口
                // rxjava必要要使用ErrorHandleSubscriber(默认实现Subscriber的onError方法),此监听才生效
                .responseErrorListener(ResponseErrorListenerImpl())
                .gsonConfiguration { _: Context?, gsonBuilder: GsonBuilder ->  //这里可以自己自定义配置Gson的参数
                    gsonBuilder
                            .serializeNulls() //支持序列化null的参数
                            .enableComplexMapKeySerialization() //支持将序列化key为object的map,默认只能序列化key为string的map
                }
                .retrofitConfiguration { _: Context?, _: Retrofit.Builder? -> }
                .okhttpConfiguration { _: Context?, builder1: OkHttpClient.Builder ->  //这里可以自己自定义配置Okhttp的参数
                    if (Config.appServer.contains("https")) {
                        supportHttps(context, builder1)
                    }
//                    builder1.hostnameVerifier(HostnameVerifier { hostname, _ -> hostname == Constant.HOST_NAME })
//                    builder.dns(object : Dns {
//                        override fun lookup(hostname: String): List<InetAddress> {
//                            val inetAddresses: MutableList<InetAddress> = mutableListOf()
//                            var hostnameAddresses: List<InetAddress>? = null
//                            try {
//                                hostnameAddresses = Dns.SYSTEM.lookup(hostname)
//                            } catch (e: UnknownHostException) {
//                                Timber.e(e)
//                            }
//                            hostnameAddresses?.let { inetAddresses.addAll(it) }
//                            try {
//                                inetAddresses.add(InetAddress.getByName("47.105.40.169:8000"))
//                            } catch (e: UnknownHostException) {
//                                Timber.e(e)
//                            }
//                            return inetAddresses
//                        }
//                    })
                    builder1.writeTimeout(10, TimeUnit.SECONDS)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .cookieJar(object : CookieJar {
                                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                                    return if (cookieStore.isNullOrEmpty()) {
                                        emptyList()
                                    } else {
                                        cookieStore[url.host()]!!
                                    }
                                }

                                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                                    for (cookie in cookies) {
                                        Timber.d("cookie:%s", cookie.toString())
                                        Timber.d("cookie value:%s", cookie.value())
                                    }
                                    //第一个cookie的值为csrftoken，第二个是sessionid
                                    //每次进行登录都会产生一个相应的csrftoken，保存这个值才可进行后续的注销登录
                                    SPUtils.getInstance().put("X-CSRFToken", cookies[0].value());
                                    cookieStore[url.host()] = cookies;
                                }

                            })
                            .addNetworkInterceptor { chain ->
                                val request = chain.request()
                                val response = chain.proceed(request)
                                var onlineCacheTime = 10L //在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
                                if (!TextUtils.isEmpty(request.header("Cache-Control"))) {
                                    onlineCacheTime = try {
                                        request.header("Cache-Control")!!.split("=")
                                            .toTypedArray()[1].toLong()
                                    } catch (e: ClassCastException) {
                                        Timber.e(e)
                                        10L
                                    }
                                }
                                response.newBuilder()
                                    .header("Cache-Control", "public, max-age=$onlineCacheTime")
                                    .removeHeader("Pragma")
                                    .build()
                            }
                        .addInterceptor { chain ->
                            var request = chain.request()
                            if (!NetworkUtils.isConnected()) {
                                request = request.newBuilder()
                                    .removeHeader("Pragma")
                                    .cacheControl(
                                        CacheControl.Builder()
                                            .onlyIfCached()
                                            .maxStale(30, TimeUnit.DAYS)
                                            .build()
                                    )
                                    .build()
                            }
                            chain.proceed(request)
                        }
                        .cache(Cache(File(cachePath), (MemoryConstants.MB * 512).toLong()))
                }
    }

    override fun injectAppLifecycle(context: Context, lifecycles: MutableList<AppLifecycles>) {
        // AppLifecycles 的所有方法都会在基类 Application 的对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        // 可以根据不同的逻辑添加多个实现类
        lifecycles.add(AppLifecycleImpl())
    }

    override fun injectActivityLifecycle(context: Context, lifecycles: MutableList<Application.ActivityLifecycleCallbacks>) {
        // ActivityLifecycleCallbacks 的所有方法都会在 Activity (包括三方库) 的对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        // 可以根据不同的逻辑添加多个实现类
        lifecycles.add(ActivityLifecycleCallbacksImpl())
    }

    override fun injectFragmentLifecycle(context: Context, lifecycles: MutableList<FragmentManager.FragmentLifecycleCallbacks>) {
        lifecycles.add(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                // 在配置变化的时候将这个 Fragment 保存下来,在 Activity 由于配置变化重建时重复利用已经创建的 Fragment。
                // https://developer.android.com/reference/android/app/Fragment.html?hl=zh-cn#setRetainInstance(boolean)
                // 如果在 XML 中使用 <Fragment/> 标签,的方式创建 Fragment 请务必在标签中加上 android:id 或者 android:tag 属性,否则 setRetainInstance(true) 无效
                // 在 Activity 中绑定少量的 Fragment 建议这样做,如果需要绑定较多的 Fragment 不建议设置此参数,如 ViewPager 需要展示较多 Fragment
                f.retainInstance = true
            }

            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {}
        })
    }

    private fun supportHttps(context: Context, okhttpBuilder: OkHttpClient.Builder) {
        val trustManager: X509TrustManager
        val sslSocketFactory: SSLSocketFactory
        var certificate: InputStream? = null
        try {
            certificate = context.assets.open("api.wmmt119.top.pem")
        } catch (e: IOException) {
            Timber.e(e)
        }
        try {
            trustManager = trustManagerForCertificates(certificate)
            val sslContext = SSLContext.getInstance("TLS")
            //使用构建出的trustManger初始化SSLContext对象
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
            //获得sslSocketFactory对象
            sslSocketFactory = sslContext.socketFactory
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
        okhttpBuilder.sslSocketFactory(sslSocketFactory, trustManager) //支持 Https,详情请百度
        Timber.e("开启https支持")
    }

    /**
     * 获去信任自签证书的trustManager
     *
     * @param in 自签证书输入流
     * @return 信任自签证书的trustManager
     * @throws GeneralSecurityException
     */
    @Throws(GeneralSecurityException::class)
    private fun trustManagerForCertificates(`in`: InputStream?): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        //通过证书工厂得到自签证书对象集合
        val certificates = certificateFactory.generateCertificates(`in`)
        require(!certificates.isEmpty()) { "expected non-empty set of trusted certificates" }
        //为证书设置一个keyStore
        val password = "password".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        var index = 0
        //将证书放入keystore中
        for (certificate in certificates) {
            val certificateAlias = Integer.toString(index++)
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }
        // Use it to build an X509 trust manager.
        //使用包含自签证书信息的keyStore去构建一个X509TrustManager
        val keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            ("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }

    @Throws(GeneralSecurityException::class)
    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        return try {
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, password)
            keyStore
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }
}