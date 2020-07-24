
package com.jess.arms.mvp;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.jess.arms.utils.ArmsUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * ================================================
 * 框架要求框架中的每个 View 都需要实现此类, 以满足规范
 * ================================================
 */
public interface IView {
    /**
     * 显示加载
     */
    default void showLoading() {

    }

    /**
     * 隐藏加载
     */
    default void hideLoading() {

    }

    /**
     * 隐藏刷新
     */
    default void hideRefresh(boolean clear) {

    }
    /**
     * 展示错误界面
     * */
    default void showError(){

    }

    /**
     * 展示内容界面
     * */
    default void showContent(){

    }

    /**
     * 展示空页面
     * */

    default void showEmpty(){

    }

    /**
     * 没有更多数据
     */
    default void showNoMoreData() {

    }

    /**
     * 显示信息
     *
     * @param message 消息内容, 不能为 {@code null}
     */
    default void showMessage(@NonNull String message){

    }

    /**
     * 跳转 {@link Activity}
     *
     * @param intent {@code intent} 不能为 {@code null}
     */
    default void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    /**
     * 杀死自己
     */
    default void killMyself() {

    }
}
