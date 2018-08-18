package com.shentu.wallpaper.mvp.ui.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.fitCenter
import com.bumptech.glide.request.RequestListener
import com.jess.arms.base.BaseFragment
import com.jess.arms.http.imageloader.glide.GlideArms
import com.jess.arms.mvp.IPresenter
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.utils.HkUtils
import kotlinx.android.synthetic.main.item_rv_category_page.*
import com.mob.commons.logcollector.e
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.shentu.wallpaper.app.utils.PicUtils
import timber.log.Timber
import java.util.*


class CategoryPageItemFragment : Fragment() {

    lateinit var picUrl: String
    var isShare: Boolean = false

    companion object {
        private const val PIC_URL = "pic_url"
        private const val IS_SHARE = "is_share"

        fun newInstance(picUrl: String, isShare: Boolean): CategoryPageItemFragment {
            val fragment = CategoryPageItemFragment()
            val args = Bundle()
            args.putBoolean(IS_SHARE, isShare)
            args.putString(PIC_URL, picUrl)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        picUrl = arguments?.getString(PIC_URL) ?: ""
        isShare = arguments?.getBoolean(IS_SHARE) ?: false
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.item_rv_category_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isShare) {
            Timber.e("Time:"+System.currentTimeMillis())
            Timber.e("shareView++++")
            ViewCompat.setTransitionName(ivPic, "shareView")
            context?.let {
                GlideArms.with(it)
                        .load(PicUtils.getInstance().getMM131GlideUrl(picUrl))
                        .placeholder(R.drawable.default_cover_vertical)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                activity?.supportStartPostponedEnterTransition()
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                activity?.supportStartPostponedEnterTransition()
                                Timber.e("Success Time:"+System.currentTimeMillis())
                                return false
                            }

                        })
                        .fitCenter()
                        .into(ivPic)
            }
        } else {
            context?.let {
                GlideArms.with(it)
                        .load(PicUtils.getInstance().getMM131GlideUrl(picUrl))
                        .placeholder(R.drawable.default_cover_vertical)
                        .fitCenter()
                        .into(ivPic)
            }
        }
    }
}