package com.micro.mvp;

import com.micro.base.BaseFragment;

/**
 * 懒加载Fragment
 */
public abstract class BaseLazyLoadFragment<P extends IPresenter> extends BaseFragment<P> {

    private boolean isDataLoaded; // 数据是否已请求

    // 实现具体的数据请求逻辑
    protected void lazyLoadData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        tryLoadData();
    }

    private void tryLoadData() {
        if (!isDataLoaded) {
            isDataLoaded = true;
            lazyLoadData();
        }
    }
}
