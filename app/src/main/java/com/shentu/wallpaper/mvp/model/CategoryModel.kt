package com.shentu.wallpaper.mvp.model

import android.app.Application
import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.shentu.wallpaper.app.BasePageModel
import com.shentu.wallpaper.model.api.cache.MicroCache
import com.shentu.wallpaper.model.api.service.MicroService
import com.shentu.wallpaper.model.response.CategoryPageResponse
import com.shentu.wallpaper.mvp.contract.CategoryContract
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import javax.inject.Inject


@FragmentScope
class CategoryModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BasePageModel(repositoryManager), CategoryContract.Model {

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun getCategories(clear: Boolean): Observable<CategoryPageResponse> {
        offset = if (clear) 0 else offset + 50
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .getCategories(offset, 50)

    }
}
