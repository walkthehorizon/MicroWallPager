
package com.jess.arms.base.delegate;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * ================================================
 * {@link FragmentDelegate} 默认实现类
 * <p>
 * Created by JessYan on 29/04/2017 16:12
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class FragmentDelegateImpl implements FragmentDelegate {
    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private IFragment iFragment;
    private Unbinder mUnbinder;


    public FragmentDelegateImpl(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        this.mFragmentManager = fragmentManager;
        this.mFragment = fragment;
        this.iFragment = (IFragment) fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (iFragment.useEventBus())//如果要使用eventbus请将此方法返回true
            EventBusManager.getInstance().register(mFragment);//注册到事件主线
        iFragment.setupFragmentComponent(ArmsUtils.obtainAppComponentFromContext(mFragment.getActivity()));
    }

    @Override
    public void onCreateView(@Nullable View view, @Nullable Bundle savedInstanceState) {
        //绑定到butterknife
        if (view != null)
            mUnbinder = ButterKnife.bind(mFragment, view);
    }

    @Override
    public void onActivityCreate(@Nullable Bundle savedInstanceState) {
        iFragment.initData(savedInstanceState);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onDestroyView() {
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            try {
                mUnbinder.unbind();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                //fix Bindings already cleared
                Timber.w("onDestroyView: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        if (iFragment != null && iFragment.useEventBus())//如果要使用eventbus请将此方法返回true
            EventBusManager.getInstance().unregister(mFragment);//注册到事件主线
        this.mUnbinder = null;
        this.mFragmentManager = null;
        this.mFragment = null;
        this.iFragment = null;
    }

    @Override
    public void onDetach() {

    }

    /**
     * Return true if the fragment is currently added to its activity.
     */
    @Override
    public boolean isAdded() {
        return mFragment != null && mFragment.isAdded();
    }
}
