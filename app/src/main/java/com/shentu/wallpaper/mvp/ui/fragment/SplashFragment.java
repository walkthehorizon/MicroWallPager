package com.shentu.wallpaper.mvp.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SpanUtils;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.IView;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.app.GlideArms;
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
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class SplashFragment extends BaseFragment<SplashPresenter> implements SplashContract.View {

    @BindView(R.id.iv_splash)
    ImageView mIvSplash;
    @BindView(R.id.mbJump)
    MaterialButton mbJump;
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
//        Objects.requireNonNull(mPresenter).getAd();
        toMainPage();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @OnClick(R.id.mbJump)
    public void clickJump() {
        toMainPage();
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
        if (splashAd.duration == 0) {
            toMainPage();
            return;
        }
        GlideArms.with(Objects.requireNonNull(getContext()))
                .load(splashAd.cover_url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        toMainPage();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startCountDown(splashAd.duration / 1000);
                        return false;
                    }
                })
                .centerCrop()
                .into(mIvSplash);
    }

    @Override
    public void toMainPage() {
        launchActivity(new Intent(getActivity(), MainActivity.class));
        killMyself();
    }

    @Override
    public void startCountDown(int total) {
        showCountDownText(total);
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle((IView) SplashFragment.this))
                .map(aLong -> total - aLong.intValue()-1)
                .take(total)
                .subscribe(new ErrorHandleSubscriber<Integer>(appComponent.rxErrorHandler()) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(Integer integer) {
                        showCountDownText(integer);
                        if (integer == 0) {
                            toMainPage();
                        }
                    }
                });
    }

    @Override
    public void showCountDownText(int time) {
        mbJump.setText(new SpanUtils()
                .append("跳过 ")
                .append(String.valueOf(time))
                .setForegroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .append(" S")
                .create());
        if (mbJump.getVisibility() == View.GONE) {
            mbJump.setVisibility(View.VISIBLE);
        }
    }
}
