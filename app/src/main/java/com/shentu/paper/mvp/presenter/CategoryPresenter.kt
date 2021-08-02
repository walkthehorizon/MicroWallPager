package com.shentu.paper.mvp.presenter

import android.app.Application

import com.micro.mvp.BasePresenter
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.response.CategoryPageResponse
import com.shentu.paper.mvp.contract.CategoryContract
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import javax.inject.Inject


class CategoryPresenter
@Inject
constructor(model: CategoryContract.Model, rootView: CategoryContract.View) :
        BasePresenter<CategoryContract.Model, CategoryContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application



    fun getCategories(clear: Boolean) {
        mModel.getCategories(clear)
                .compose(RxUtils.applySchedulers(mRootView, clear))
                .subscribe(object : ErrorHandleSubscriber<CategoryPageResponse>(mErrorHandler) {
                    override fun onNext(t: CategoryPageResponse) {
                        mRootView.showCategories(t.data?.content, clear)
                    }
                })
    }

}

