package com.shentu.wallpaper.mvp.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.event.LoadOriginPictureEvent
import com.shentu.wallpaper.app.event.LoadOriginResultEvent
import com.shentu.wallpaper.app.event.SwitchNavigationEvent
import com.shentu.wallpaper.app.utils.PicUtils
import com.shentu.wallpaper.di.component.DaggerPictureBrowserComponent
import com.shentu.wallpaper.di.module.PictureBrowserModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.PictureBrowserContract
import com.shentu.wallpaper.mvp.presenter.PictureBrowserPresenter
import com.shentu.wallpaper.mvp.ui.activity.PictureBrowserActivity
import com.shentu.wallpaper.mvp.ui.activity.PictureBrowserActivity.Companion.SUBJECT_ID
import com.shentu.wallpaper.mvp.ui.adapter.PictureBrowserVpAdapter
import kotlinx.android.synthetic.main.fragment_picture_browser.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


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
    private lateinit var wallpapers: MutableList<Wallpaper>

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
                .pictureBrowserModule(PictureBrowserModule(this, mContext))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_picture_browser, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        subjectId = arguments?.getInt(SUBJECT_ID)!!
        viewPager.addOnPageChangeListener(this)
        viewPager.offscreenPageLimit = 4
        mPresenter?.getPictures(subjectId)

        ivDownload.setOnClickListener {
            FileDownloader.getImpl().create(wallpapers[viewPager.currentItem].origin_url)
                    .setPath(PathUtils.getExternalPicturesPath()).listener = object : FileDownloadListener() {
                override fun warn(task: BaseDownloadTask?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun completed(task: BaseDownloadTask?) {
                    showMessage("文件已存储：" + task!!.targetFilePath)
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    if (e != null) {
                        showMessage("下载失败：" + e.message)
                    }
                }

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            }
        }
        mbLoadOrigin.setOnClickListener {
            mbLoadOrigin.isEnabled = false//在结果回调前禁用二次点击
            EventBus.getDefault().post(LoadOriginPictureEvent(wallpapers[viewPager.currentItem].id))
        }
        ivDownload.setOnClickListener {
            ivDownload.visibility = View.GONE
            mPresenter?.downloadPicture(wallpapers[viewPager.currentItem].origin_url!!)
        }
    }

    override fun setData(data: Any?) {

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
        wallpapers = pictures
        viewPager.offscreenPageLimit = 5
        vpAdapter = PictureBrowserVpAdapter(childFragmentManager, pictures)
        viewPager.adapter = vpAdapter
        onPageSelected(0)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onPageSelected(position: Int) {
        tvOrder.text = "${position + 1}/${vpAdapter.count}"
        mbLoadOrigin.visibility = if (wallpapers[position].isOriginExist) View.GONE else View.VISIBLE
        ivDownload.visibility = if (FileUtils.isFileExists(PicUtils.getInstance().getDownloadPicturePath(
                        wallpapers[position].origin_url))) View.GONE else View.VISIBLE
    }

    override fun showNavigation() {
        rl_head.visibility = View.VISIBLE
        rl_bottom.visibility = View.VISIBLE
    }

    override fun hideNavigation() {
        rl_head.visibility = View.GONE
        rl_bottom.visibility = View.GONE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun switchNavigation(event: SwitchNavigationEvent) {
        if (rl_head.visibility == View.VISIBLE) {
            hideNavigation()
        } else {
            showNavigation()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadOriginResult(event: LoadOriginResultEvent) {
        for (wallpaper in wallpapers) {
            if (wallpaper.id == event.id) {
                if (event.result) {
                    mbLoadOrigin.visibility = View.GONE
                    wallpaper.isOriginExist = true
                } else {
                    mbLoadOrigin.isEnabled = true
                }
            }
        }
    }
}
