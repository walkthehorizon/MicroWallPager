package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.entity.MicroUser
import com.shentu.wallpaper.model.response.BaseResponse
import com.shentu.wallpaper.model.response.SplashAdResponse
import com.shentu.wallpaper.mvp.contract.SplashContract
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import io.rx_cache2.EvictProvider
import io.rx_cache2.Reply
import javax.inject.Inject

@FragmentScope
class SplashModel @Inject constructor(repositoryManager: IRepositoryManager?) : BaseModel(repositoryManager), SplashContract.Model {
    @JvmField
    @Inject
    var mGson: Gson? = null
    @JvmField
    @Inject
    var mApplication: Application? = null

    override fun onDestroy() {
        super.onDestroy()
        mGson = null
        mApplication = null
    }

//    override fun getSplashAd(): Observable<SplashAdResponse> {
//        return Observable.just(mRepositoryManager
//                .obtainRetrofitService(MicroService::class.java)
//                .splash)
//                .flatMap { t ->
//                    mRepositoryManager.obtainCacheService(MicroCache::class.java)
//                            .getSplashAd(t, EvictProvider(false))
//                            .map { it.data }
//                }
//    }
}