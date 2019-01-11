package com.shentu.wallpaper.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.integration.AppManager;
import com.jess.arms.utils.ArmsUtils;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.app.utils.RxUtils;
import com.shentu.wallpaper.di.component.DaggerSplashComponent;
import com.shentu.wallpaper.di.module.SplashModule;
import com.shentu.wallpaper.model.entity.SplashAd;
import com.shentu.wallpaper.mvp.contract.SplashContract;
import com.shentu.wallpaper.mvp.presenter.SplashPresenter;
import com.shentu.wallpaper.mvp.ui.activity.MainActivity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class SplashFragment extends BaseFragment<SplashPresenter> implements SplashContract.View {

    @BindView(R.id.iv_splash)
    ImageView mIvSplash;
    @Inject
    AppManager appManager;

    private AppComponent appComponent;

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
        this.appComponent = appComponent;
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_fragment_splash, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(mPresenter).getAd();

    }

    @Override
    public void setData(@Nullable Object data) {

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
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void showSplash(SplashAd splashAd) {
        Timber.e("showSplash");
        GlideArms.with(Objects.requireNonNull(getContext()))
                .load(splashAd.cover_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(listener)
                .centerCrop()
                .into(mIvSplash);
    }

    @Override
    public void toMainPage() {
        Observable.timer(2, TimeUnit.SECONDS)
                .compose(RxUtils.applyClearSchedulers(this))
                .subscribe(new ErrorHandleSubscriber<Long>(appComponent.rxErrorHandler()) {
                    @Override
                    public void onNext(Long aLong) {
                        launchActivity(new Intent(getActivity(), MainActivity.class));
                        killMyself();
                    }
                });
    }

    private RequestListener listener = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
            toMainPage();
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
            toMainPage();
            return false;
        }
    };
}
