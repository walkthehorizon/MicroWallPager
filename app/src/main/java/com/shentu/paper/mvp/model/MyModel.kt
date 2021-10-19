package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.micro.integration.IRepositoryManager
import com.micro.mvp.BaseModel
import com.shentu.paper.mvp.contract.MyContract
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject


@FragmentScoped
class MyModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), MyContract.Model {
    @Inject
    lateinit var mGson: Gson

    @Inject
    lateinit var mApplication: Application

    override fun onDestroy() {
        super.onDestroy()
    }
}
