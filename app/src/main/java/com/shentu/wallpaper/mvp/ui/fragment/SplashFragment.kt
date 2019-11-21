package com.shentu.wallpaper.mvp.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.blankj.utilcode.util.SpanUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IView
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.Preconditions.checkNotNull
import com.jess.arms.utils.RxLifecycleUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.di.component.DaggerSplashComponent
import com.shentu.wallpaper.di.module.SplashModule
import com.shentu.wallpaper.model.entity.SplashAd
import com.shentu.wallpaper.mvp.contract.SplashContract
import com.shentu.wallpaper.mvp.presenter.SplashPresenter
import com.shentu.wallpaper.mvp.ui.activity.MainActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_fragment_splash.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import java.util.concurrent.TimeUnit


class SplashFragment : BaseFragment<SplashPresenter>(), SplashContract.View {

    private var appComponent: AppComponent? = null

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerSplashComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .splashModule(SplashModule(this))
                .build()
                .inject(this)
        this.appComponent = appComponent
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.app_fragment_splash, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Objects.requireNonNull(mPresenter).getAd()
        toMainPage()
    }

    override fun setData(data: Any?) {

    }

    @OnClick(R.id.mbJump)
    fun clickJump() {
        toMainPage()
    }

    override fun showMessage(message: String) {
        checkNotNull(message)
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        activity?.finish()
    }

    override fun showSplash(splashAd: SplashAd) {
        if (splashAd.duration == 0) {
            toMainPage()
            return
        }
        context?.let {
            Glide.with(it)
                    .load(splashAd.cover_url)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            toMainPage()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            startCountDown(splashAd.duration / 1000)
                            return false
                        }
                    })
                    .centerCrop()
                    .into(ivSplash)
        }
    }

    private fun loadAd() {

    }

    private fun showAd() {

    }

    override fun toMainPage() {
        launchActivity(Intent(mContext, MainActivity::class.java))
        killMyself()
    }

    override fun startCountDown(total: Int) {
        showCountDownText(total)
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(this@SplashFragment as IView))
                .map { aLong -> total - aLong.toInt() - 1 }
                .take(total.toLong())
                .subscribe(object : ErrorHandleSubscriber<Int>(appComponent!!.rxErrorHandler()) {
                    @SuppressLint("SetTextI18n")
                    override fun onNext(integer: Int) {
                        showCountDownText(integer)
                        if (integer == 0) {
                            toMainPage()
                        }
                    }
                })
    }

    override fun showCountDownText(time: Int) {
        mbJump!!.text = SpanUtils()
                .append("跳过 ")
                .append(time.toString())
                .setForegroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .append(" S")
                .create()
        if (mbJump!!.visibility == View.GONE) {
            mbJump!!.visibility = View.VISIBLE
        }
    }

    companion object {

        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }
}
