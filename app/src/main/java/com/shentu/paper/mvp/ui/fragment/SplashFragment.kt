package com.shentu.paper.mvp.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.micro.base.BaseFragment
import com.micro.mvp.IPresenter
import com.micro.mvp.IView
import com.micro.utils.ArmsUtils
import com.micro.utils.Preconditions.checkNotNull
import com.micro.utils.RxLifecycleUtils
import com.shentu.paper.R
import com.shentu.paper.mvp.contract.SplashContract
import com.shentu.paper.mvp.ui.activity.MainActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_fragment_splash.*
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashFragment : BaseFragment<IPresenter>(), SplashContract.View {

    @Inject
    lateinit var errorHandler:RxErrorHandler

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

    @OnClick(R.id.mbJump)
    fun clickJump() {
        toMainPage()
    }

    override fun showMessage(message: String) {
        checkNotNull(message)
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        checkNotNull(intent)
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        activity?.finish()
    }

//    override fun showSplash(splashAd: SplashAd) {
//        if (splashAd.duration == 0) {
//            toMainPage()
//            return
//        }
//        context?.let {
//            GlideApp.with(it)
//                    .load(splashAd.cover_url)
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
//                            toMainPage()
//                            return false
//                        }
//
//                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
//                            startCountDown(splashAd.duration / 1000)
//                            return false
//                        }
//                    })
//                    .centerCrop()
//                    .into(ivSplash)
//        }
//    }

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
                .subscribe(object : ErrorHandleSubscriber<Int>(errorHandler) {
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
