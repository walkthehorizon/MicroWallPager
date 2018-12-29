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

import com.jess.arms.mvp.IView;
import com.jess.arms.utils.RxLifecycleUtils;
import com.shentu.wallpaper.model.entity.BasePageResponse;
import com.shentu.wallpaper.model.entity.BaseResponse;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * ================================================
 * 放置便于使用 RxJava 的一些工具方法
 * <p>
 * Created by JessYan on 11/10/2016 16:39
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class RxUtils {

    private RxUtils() {
    }

    //用于无需状态控制的observable
    public static <T> ObservableTransformer<T, T> applyClearSchedulers(final IView view) {
        return observable -> observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(view));
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(final IView view) {
        return observable -> {
            //隐藏进度条
            return observable.subscribeOn(Schedulers.io())
                    .doOnSubscribe(disposable -> view.showLoading())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(t -> handleOnNext(t, view))
                    .doOnError(throwable -> {
                        view.showError();
                        view.hideLoading();
                    })
                    .compose(RxLifecycleUtils.bindToLifecycle(view));
        };
    }

    public static <T> ObservableTransformer<T, T> applySchedulers(final IView view, boolean clear) {
        return observable -> {
            //隐藏进度条
            return observable.subscribeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(t -> {
                        handleOnNext(t, view);
                        view.hideRefresh(clear);
                    })
                    .doOnError(throwable -> {
                        view.showError();
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
        if (((BaseResponse) t).isSuccess()) {
            //有些接口好像不统一
            if (isDataEmpty((BaseResponse) t)) {
                view.showEmpty();
            } else {
                view.showContent();
            }
        }
    }

    private static boolean isDataEmpty(BaseResponse t) {
        return t.getData() == null || (t.getData() instanceof BasePageResponse
                && ((BasePageResponse) t.getData()).getCount() == 0);
    }
}
