
package com.shentu.wallpaper.app.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.mvp.IView;
import com.jess.arms.utils.RxLifecycleUtils;
import com.shentu.wallpaper.model.response.BasePageResponse;
import com.shentu.wallpaper.model.response.BaseResponse;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.RxCacheException;

/**
 * 放置便于使用 RxJava 的一些工具方法
 */
public class RxUtils {

    private RxUtils() {
    }

    //用于无需状态控制的observable
    public static <T> ObservableTransformer<T, T> applyClearSchedulers(final IView view) {
        return observable -> observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(RxLifecycleUtils.bindToLifecycle(view));
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(final IView view) {
        return observable -> {
            //隐藏进度条
            return observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnNext(t -> handleOnNext(t, view))
                    .doOnError(throwable -> view.showError())
                    .doFinally(view::hideLoading)
//                    .doOnSubscribe(disposable -> view.showLoading())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .compose(RxLifecycleUtils.bindToLifecycle(view));
        };
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(final IView view, boolean clear) {
        return observable -> {
            //隐藏进度条
            return observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
//                    .doOnSubscribe(disposable -> view.showLoading())
                    .doOnNext(t -> handleOnNext(t, view))
                    .doOnError(throwable -> {
                        if (clear) {
                            view.showError();
                        }
                        //特殊处理缓存异常
                        if (throwable instanceof RxCacheException) {
                            ToastUtils.showShort("缓存读取异常");
                        }
                    })
                    .doFinally(() -> {
                        view.hideRefresh(clear);
                        view.hideLoading();
                    })
                    .compose(RxLifecycleUtils.bindToLifecycle(view));
        };
    }

    /**
     * 接收数据前进行统一处理，目标fragment/activity需实现IView
     */
    private static <T> void handleOnNext(T t, IView view) {
        if (!(t instanceof BaseResponse)) {
            view.showContent();
            return;
        }
        BaseResponse response = (BaseResponse) t;
        if (response.isSuccess()) {
            //检查是否为列表
            BasePageResponse pageResponse = null;
            if (((BaseResponse) t).getData() instanceof BasePageResponse) {
                pageResponse = (BasePageResponse) ((BaseResponse) t).getData();
            }
            if (pageResponse != null && pageResponse.getCount() == 0) {
                view.showEmpty();
                return;
            }
            if (pageResponse != null && TextUtils.isEmpty(pageResponse.getNext())) {
                view.showNOMoreData();
            }
            view.showContent();
        } else {
            ToastUtils.showShort(((BaseResponse) t).getMsg());
            view.showError();
        }
    }
}
