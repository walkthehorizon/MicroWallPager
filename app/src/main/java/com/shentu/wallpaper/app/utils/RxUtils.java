/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
                    .doOnSubscribe(disposable -> view.showLoading())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .compose(RxLifecycleUtils.bindToLifecycle(view));
        };
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(final IView view, boolean clear) {
        return observable -> {
            //隐藏进度条
            return observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(disposable -> view.showLoading())
                    .doOnNext(t -> handleOnNext(t, view))
                    .doOnError(throwable -> view.showError())
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
