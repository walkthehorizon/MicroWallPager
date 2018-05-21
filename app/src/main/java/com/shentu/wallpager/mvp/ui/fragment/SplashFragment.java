package com.shentu.wallpager.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.integration.AppManager;
import com.jess.arms.integration.lifecycle.Lifecycleable;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.shentu.wallpager.R;
import com.shentu.wallpager.di.component.DaggerSplashComponent;
import com.shentu.wallpager.di.module.SplashModule;
import com.shentu.wallpager.mvp.contract.SplashContract;
import com.shentu.wallpager.mvp.model.entity.SplashAd;
import com.shentu.wallpager.mvp.presenter.SplashPresenter;
import com.shentu.wallpager.mvp.ui.activity.MainActivity;
import com.shentu.wallpager.mvp.ui.activity.SplashActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class SplashFragment extends BaseFragment<SplashPresenter> implements SplashContract.View {

    @BindView(R.id.iv_splash)
    ImageView mIvSplash;
    @Inject
    AppManager appManager;

    public static SplashFragment newInstance() {
        SplashFragment fragment = new SplashFragment();
        return fragment;
    }


    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerSplashComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .splashModule(new SplashModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_fragment_splash, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPresenter.getAd();
        Observable.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .compose(RxLifecycleUtils.bindToLifecycle((Lifecycleable) SplashFragment.this))
                .subscribe(aLong -> {
                    launchActivity(new Intent(getActivity(), MainActivity.class));
                    killMyself();
                });

        Timber.e("initData");
    }

    /**
     * 通过此方法可以使 Fragment 能够与外界做一些交互和通信, 比如说外部的 Activity 想让自己持有的某个 Fragment 对象执行一些方法,
     * 建议在有多个需要与外界交互的方法时, 统一传 {@link Message}, 通过 what 字段来区分不同的方法, 在 {@link #setData(Object)}
     * 方法中就可以 {@code switch} 做不同的操作, 这样就可以用统一的入口方法做多个不同的操作, 可以起到分发的作用
     * <p>
     * 调用此方法时请注意调用时 Fragment 的生命周期, 如果调用 {@link #setData(Object)} 方法时 {@link Fragment#onCreate(Bundle)} 还没执行
     * 但在 {@link #setData(Object)} 里却调用了 Presenter 的方法, 是会报空的, 因为 Dagger 注入是在 {@link Fragment#onCreate(Bundle)} 方法中执行的
     * 然后才创建的 Presenter, 如果要做一些初始化操作,可以不必让外部调用 {@link #setData(Object)}, 在 {@link #initData(Bundle)} 中初始化就可以了
     * <p>
     * Example usage:
     * <pre>
     * public void setData(@Nullable Object data) {
     *     if (data != null && data instanceof Message) {
     *         switch (((Message) data).what) {
     *             case 0:
     *                 loadData(((Message) data).arg1);
     *                 break;
     *             case 1:
     *                 refreshUI();
     *                 break;
     *             default:
     *                 //do something
     *                 break;
     *         }
     *     }
     * }
     *
     * // call setData(Object):
     * Message data = new Message();
     * data.what = 0;
     * data.arg1 = 1;
     * fragment.setData(data);
     * </pre>
     *
     * @param data 当不需要参数时 {@code data} 可以为 {@code null}
     */
    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        appManager.killActivity(SplashActivity.class);
    }

    @Override
    public void showSplash(SplashAd splashAd) {
        Timber.e("showSplash");
        splashAd.cover_url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526466679138&di=4809ac4739129945969fcb0ca173fc73&imgtype=0&src=http%3A%2F%2Fuploads.xuexila.com%2Fallimg%2F1707%2F28-1FG11H501.jpg";
        GlideArms.with(getContext())
                .load(splashAd.cover_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mIvSplash);
    }
}
