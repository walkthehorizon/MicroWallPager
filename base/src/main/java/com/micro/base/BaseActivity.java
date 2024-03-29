package com.micro.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;

import com.horizon.netbus.NetBus;
import com.horizon.netbus.NetType;
import com.horizon.netbus.NetWork;
import com.micro.base.delegate.IActivity;
import com.micro.integration.cache.Cache;
import com.micro.integration.lifecycle.ActivityLifecycleable;
import com.micro.mvp.IPresenter;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleObserver;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * ================================================
 * 因为 Java 只能单继承, 所以如果要用到需要继承特定 {@link Activity} 的三方库, 那你就需要自己自定义 {@link Activity}
 * 继承于这个特定的 {@link Activity}, 然后再按照 {@link BaseActivity} 的格式, 将代码复制过去, 记住一定要实现{@link IActivity}
 */
public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity implements IActivity, ActivityLifecycleable {
    protected final String TAG = this.getClass().getSimpleName();
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    private Unbinder mUnbinder;
    @Inject
    @Nullable
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null

    @Inject
    Cache<String, Object> mCache;

    @NonNull
    @Override
    public synchronized Cache<String, Object> provideCache() {
        return mCache;
    }

    @NonNull
    @Override
    public final Subject<ActivityEvent> provideLifecycleSubject() {
        return mLifecycleSubject;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            int layoutResID = initView(savedInstanceState);
            //如果initView返回0,框架则不会调用setContentView(),当然也不会 Bind ButterKnife
            if (layoutResID != 0) {
                setContentView(layoutResID);
                //绑定到butterknife
                mUnbinder = ButterKnife.bind(this);
            }
        } catch (Exception e) {
            if (e instanceof InflateException) throw e;
            e.printStackTrace();
        }
        if (mPresenter != null) {
            getLifecycle().addObserver((LifecycleObserver) mPresenter);
        }
        //如果要使用 EventBus 请将此方法返回 true
        if (useEventBus()){
            //注册到事件主线
            EventBus.getDefault().register(this);
        }
//        snackbar = TSnackbar.make(findViewById(android.R.id.content), "网络已断开连接", Snackbar.LENGTH_INDEFINITE);
        initData(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果要使用 EventBus 请将此方法返回 true
        if (useEventBus())
            EventBus.getDefault().unregister(this);
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        this.mUnbinder = null;
        if (mPresenter != null)
            mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }

    /**
     * 是否使用 EventBus
     *
     * @return 返回 {@code true} (默认为使用 {@code true}), Arms 会自动注册 EventBus
     */
    @Override
    public boolean useEventBus() {
        return false;
    }

    /**
     * 这个Activity是否会使用Fragment,框架会根据这个属性判断是否注册{@link FragmentManager.FragmentLifecycleCallbacks}
     * 如果返回false,那意味着这个Activity不需要绑定Fragment,那你再在这个Activity中绑定继承于 {@link com.micro.base.BaseFragment} 的Fragment将不起任何作用
     *
     * @return
     */
    @Override
    public boolean useFragment() {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        NetBus.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        NetBus.getInstance().unRegister(this);
    }

//    private TSnackbar snackbar;

    @NetWork
    public void onNetChange(NetType type) {
        switch (type) {
            case WIFI:
            case GPRS:
//                snackbar.dismiss();
                break;
            case NONE:
//                snackbar.show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
