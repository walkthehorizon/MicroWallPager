package com.shentu.paper.mvp.presenter

import android.app.Application
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.integration.AppManager
import com.jess.arms.mvp.BasePresenter
import com.shentu.paper.app.utils.LimitQueue
import com.shentu.paper.app.utils.RxUtils
import com.shentu.paper.model.response.SubjectPageResponse
import com.shentu.paper.mvp.contract.SearchContract
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@ActivityScope
class SearchPresenter
@Inject
constructor(model: SearchContract.Model, rootView: SearchContract.View) :
        BasePresenter<SearchContract.Model, SearchContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mAppManager: AppManager
    @Inject
    lateinit var gson: Gson

    private lateinit var observable: Observable<String>
    private lateinit var mEmitter: ObservableEmitter<String>
    private var curKey: String = ""
    private lateinit var keyQueue: LimitQueue<String>

    fun init() {
        val history = SPUtils.getInstance().getString("search_history", "")
        Timber.e(history)
        keyQueue = if (history.isEmpty()) {
            LimitQueue(12)
        } else {
            gson.fromJson(history, object : TypeToken<LimitQueue<String>>() {}.type)
        }
        Timber.e(keyQueue.queue.toString())

        //首次搜索key
        observable = Observable.create { emitter ->
            mEmitter = emitter
        }
        observable.debounce(500, TimeUnit.MILLISECONDS)
                .filter {
                    if (it.isEmpty()) {
                        mRootView.showHistory(keyQueue)
                        return@filter false
                    }
                    keyQueue.offer(curKey, true)
                    SPUtils.getInstance().put("search_history", gson.toJson(keyQueue))
                    return@filter true
                }.doOnNext {
                    mRootView.showLoading()
                }
                .switchMap {
                    Timber.e("load data: $it")
                    mModel.searchKey(it, true)
                }
                .compose(RxUtils.applySchedulers(mRootView, true))
                .subscribe(object : ErrorHandleSubscriber<SubjectPageResponse>(mErrorHandler) {
                    override fun onNext(t: SubjectPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showResults(it, true) }
                    }
                })

        mRootView.showHistory(keyQueue)//默认展示历史记录
    }

    /**
     * debounce操作符会自动过滤当前搜索项，所以要实现当前项更多数据显示这里要独立加载
     * */
    fun loadMore() {
        mModel.searchKey(curKey, false)
                .compose(RxUtils.applySchedulers(mRootView, false))
                .subscribe(object : ErrorHandleSubscriber<SubjectPageResponse>(mErrorHandler) {
                    override fun onNext(t: SubjectPageResponse) {
                        if (!t.isSuccess) {
                            return
                        }
                        t.data?.content?.let { mRootView.showResults(it, false) }
                    }
                })
    }

    /**
     * 搜索key
     * */
    fun search(key: String) {
        curKey = key
        mEmitter.onNext(curKey)
    }

    fun clearHistory() {
        keyQueue.queue.clear()
        SPUtils.getInstance().remove("search_history")
    }
}
