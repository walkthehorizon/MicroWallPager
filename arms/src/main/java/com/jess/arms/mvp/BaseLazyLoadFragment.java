package com.jess.arms.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jess.arms.base.BaseFragment;

import java.util.List;

import timber.log.Timber;

/**
 * 懒加载Fragment
 */
public abstract class BaseLazyLoadFragment<P extends IPresenter> extends BaseFragment<P> {

    private boolean isViewCreated; // 界面是否已创建完成
    private boolean isVisibleToUser; // 是否对用户可见
    private boolean isDataLoaded; // 数据是否已请求, isNeedReload()返回false的时起作用

    // 实现具体的数据请求逻辑
    protected void lazyLoadData() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        tryLoadData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.e(this.toString() + "-onActivityCreated1111");
    }

    @Override
    public void onResume() {
        super.onResume();
        isViewCreated = true;
        tryLoadData();
    }

    /**
     * ViewPager场景下，判断父fragment是否可见
     *
     * @return
     */
    private boolean isParentVisible() {
        Fragment fragment = getParentFragment();
        return fragment == null || (fragment instanceof BaseLazyLoadFragment && ((BaseLazyLoadFragment) fragment).isVisibleToUser);
    }

    /**
     * ViewPager场景下，当前fragment可见，如果其子fragment也可见，则尝试让子fragment请求数据
     */
    private void dispatchParentVisibleState() {
        FragmentManager fragmentManager = getChildFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments.isEmpty()) {
            return;
        }
        for (Fragment child : fragments) {
            if (child instanceof BaseLazyLoadFragment && ((BaseLazyLoadFragment) child).isVisibleToUser) {
                ((BaseLazyLoadFragment) child).tryLoadData();
            }
        }
    }

    public void tryLoadData() {
        if (isViewCreated && isVisibleToUser && isParentVisible() && !isDataLoaded) {
            lazyLoadData();
            isDataLoaded = true;
            // 通知 子 Fragment 请求数据
            dispatchParentVisibleState();
        }
    }
}
