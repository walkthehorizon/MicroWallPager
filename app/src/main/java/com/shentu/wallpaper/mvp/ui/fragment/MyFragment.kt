package com.shentu.wallpaper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.GlideArms
import com.shentu.wallpaper.app.HkUserManager
import com.shentu.wallpaper.app.event.LoginSuccessEvent
import com.shentu.wallpaper.di.component.DaggerMyComponent
import com.shentu.wallpaper.di.module.MyModule
import com.shentu.wallpaper.mvp.contract.MyContract
import com.shentu.wallpaper.mvp.presenter.MyPresenter
import com.shentu.wallpaper.mvp.ui.activity.SettingMoreActivity
import com.tencent.bugly.beta.Beta
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_my.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MyFragment : BaseFragment<MyPresenter>(), MyContract.View {
    companion object {
        fun newInstance(): MyFragment {
            val fragment = MyFragment()
            return fragment
        }
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerMyComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .myModule(MyModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_my, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        if (HkUserManager.getInstance().isLogined) {
            //TODO 获取用户数据
            loadUserData(null)
        }
        if (Beta.getUpgradeInfo() != null) {
            item_update.setEndValue("有新的升级可用")
        }
        rlHead.setOnClickListener {clickHead()}
        item_collect.setOnClickListener { clickCollect() }
        item_browser.setOnClickListener {clickBrowser()}
        item_update.setOnClickListener{
            Beta.checkUpgrade()
        }
        item_cache.setOnClickListener {clickCache()}
        item_feedback.setOnClickListener{clickFeedback()}
        item_more.setOnClickListener{
            startActivity(Intent(context,SettingMoreActivity::class.java))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loadUserData(event: LoginSuccessEvent?) {
        tvMyName.text = HkUserManager.getInstance().user.nickname
        GlideArms.with(this)
                .load(HkUserManager.getInstance().user.avatar)
                .into(circle_avatar)
    }

    fun clickHead() {
        HkUserManager.getInstance().checkLogin(context)
    }

    fun clickCollect() {
        HkUserManager.getInstance().checkLogin(context)
    }

    fun clickBrowser() {
        ToastUtils.showShort("待完善")
    }

    fun clickCache() {
        Glide.get(context!!).clearMemory()
        Observable.just(0)
                .observeOn(Schedulers.io())
                .doOnSubscribe { showLoading() }
                .doFinally { hideLoading() }
                .doOnComplete { ToastUtils.showShort("清理成功") }
                .subscribe { Glide.get(context!!).clearDiskCache() }
    }

    fun clickFeedback() {
        HkUserManager.getInstance().checkLogin(context)
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
     *fun setData(data:Any) {
     *   if(data is Message){
     *       when (data.what) {
     *           0-> {
     *               //根据what 做你想做的事情
     *           }
     *           else -> {
     *           }
     *       }
     *   }
     *}
     *
     *
     *
     *
     *
     * // call setData(Object):
     * val data = Message();
     * data.what = 0;
     * data.arg1 = 1;
     * fragment.setData(data);
     * </pre>
     *
     * @param data 当不需要参数时 {@code data} 可以为 {@code null}
     */
    override fun setData(data: Any?) {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }
}
