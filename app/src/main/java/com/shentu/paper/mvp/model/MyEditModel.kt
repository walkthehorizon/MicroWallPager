package com.shentu.paper.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.mvp.contract.MyEditContract
import io.reactivex.Observable
import javax.inject.Inject


@ActivityScope
class MyEditModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), MyEditContract.Model {

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun updateUser(user: MicroUser): Observable<MicroUser> {
        return mRepositoryManager
                .obtainRetrofitService(MicroService::class.java)
                .updateUser(user, user.uid)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
