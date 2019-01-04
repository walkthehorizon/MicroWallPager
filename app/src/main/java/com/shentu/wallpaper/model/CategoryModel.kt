package com.shentu.wallpaper.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.wallpaper.mvp.contract.CategoryContract
import javax.inject.Inject


@FragmentScope
class CategoryModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), CategoryContract.Model {


    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

//    override fun getCategorys(): Observable<BasePageResponse<Category>> {
//        return mRepositoryManager
//                .obtainRetrofitService(MicroService::class.java)
//                .categorys
//    }
}
