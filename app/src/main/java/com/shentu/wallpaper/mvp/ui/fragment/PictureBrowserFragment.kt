package com.shentu.wallpaper.mvp.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import com.shentu.wallpaper.di.component.DaggerPictureBrowserComponent
import com.shentu.wallpaper.di.module.PictureBrowserModule
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.shentu.wallpaper.mvp.presenter.PictureBrowserPresenter

import com.shentu.wallpaper.R

import com.shentu.wallpaper.app.EventBusTags
import com.shentu.wallpaper.app.event.SwitchNavigationEvent
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.ui.activity.PictureBrowserActivity
import com.shentu.wallpaper.mvp.ui.activity.PictureBrowserActivity.Companion.SUBJECT_ID
import com.shentu.wallpaper.mvp.ui.adapter.PictureBrowserVpAdapter
import kotlinx.android.synthetic.main.fragment_picture_browser.*
import org.simple.eventbus.Subscriber
import javax.inject.Inject


/**
 * 如果没presenter
 * 你可以这样写
 *
 * @FragmentScope(請注意命名空間) class NullObjectPresenterByFragment
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class PictureBrowserFragment : BaseFragment<PictureBrowserPresenter>(), PictureBrowserContract.View, ViewPager.OnPageChangeListener {

    private lateinit var vpAdapter: PictureBrowserVpAdapter

    private var subjectId: Int = 0

    companion object {
        fun newInstance(subjectId: Int): PictureBrowserFragment {
            val fragment = PictureBrowserFragment()
            val args = Bundle()
            args.putInt(PictureBrowserActivity.SUBJECT_ID, subjectId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerPictureBrowserComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .pictureBrowserModule(PictureBrowserModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_picture_browser, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        subjectId = arguments?.getInt(SUBJECT_ID)!!
        viewPager.addOnPageChangeListener(this)
        viewPager.offscreenPageLimit = 4
        mPresenter?.getPictures(subjectId)
    }

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
        activity?.finish()
    }

    override fun showPictures(pictures: MutableList<Wallpaper>) {
        vpAdapter = PictureBrowserVpAdapter(pictures)
        viewPager.adapter = vpAdapter
        onPageSelected(0)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    @SuppressLint("SetTextI18n")
    override fun onPageSelected(position: Int) {
        tvOrder.text = "${position + 1}/${vpAdapter.count}"
    }

    override fun showNavigation() {
        rl_head.visibility = View.VISIBLE
        rl_bottom.visibility = View.VISIBLE
    }

    override fun hideNavigation() {
        rl_head.visibility = View.GONE
        rl_bottom.visibility = View.GONE
    }

    @Subscriber(tag = EventBusTags.SWITCH_BROWSE_NAVIGATION)
    fun switchNavigation(event: SwitchNavigationEvent){
        if (rl_head.visibility == View.VISIBLE) {
            hideNavigation()
        } else {
            showNavigation()
        }
    }
}
