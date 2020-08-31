package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.paper.mvp.contract.SplashContract
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