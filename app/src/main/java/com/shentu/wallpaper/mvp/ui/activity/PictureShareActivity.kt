package com.shentu.wallpaper.mvp.ui.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.mvp.IPresenter
import com.shentu.wallpaper.R
import com.shentu.wallpaper.app.Constant
import com.shentu.wallpaper.app.utils.HkUtils
import com.shentu.wallpaper.app.utils.PicUtils
import com.shentu.wallpaper.model.entity.Wallpaper
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.layout_custom_share.*

@Route(path = "/activity/picture/share")
class PictureShareActivity : BaseActivity<IPresenter>() {

    @JvmField
    @Autowired
    var paper: Wallpaper = Wallpaper()

    private lateinit var cardBitmap: Bitmap

    override fun setupActivityComponent(appComponent: AppComponent) {

    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_share
    }

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        Glide.with(this)
                .load(paper.url)
                .transform(RoundedCornersTransformation(ConvertUtils.dp2px(10f)
                        , 0, RoundedCornersTransformation.CornerType.TOP))
                .into(ivShare)
        ivCode.post {
            val qrBitmap = HkUtils.instance.createQRImage(Constant.BASE_WALLPAPER_SHARE_URL + paper.id
                    , ivCode.width, ivCode.height)
            ivCode.setImageBitmap(qrBitmap)
        }
        tvTitle.text = "《${paper.title}》"

        llShare.post {
            val animator = ObjectAnimator.ofFloat(llShare, View.TRANSLATION_Y, 100f, 0f)
            animator.duration = 300
            animator.start()
        }

        tvWxMoment.setOnClickListener { showShare(WechatMoments.NAME) }
        tvWx.setOnClickListener { showShare(Wechat.NAME) }
        tvQQ.setOnClickListener { showShare(QQ.NAME) }
        tvDownload.setOnClickListener {
            FileIOUtils.writeFileFromBytesByStream(PicUtils.getInstance().getDownloadSharePath(
                    paper.url), ImageUtils.bitmap2Bytes(getCardBitmap(), Bitmap.CompressFormat.JPEG))
            ToastUtils.showShort("图片已保存在 手机相册》萌幻Cos")
        }
    }

    private fun showShare(platform: String) {
        val oks = OnekeyShare()
        oks.setPlatform(platform)
        if (platform == QQ.NAME) {
            val sharePath: String = PicUtils.getInstance().getDownloadSharePath(paper.url)
            FileIOUtils.writeFileFromBytesByStream(sharePath, ImageUtils.bitmap2Bytes(getCardBitmap(), Bitmap.CompressFormat.JPEG))
            oks.setImagePath(sharePath)
        } else {
            oks.setImageData(getCardBitmap())
        }
        oks.show(this)
    }

    private fun getCardBitmap(): Bitmap {
        if (!this::cardBitmap.isInitialized) {
            cardBitmap = Bitmap.createBitmap(flShare.width, flShare.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(cardBitmap)
            flShare.draw(canvas)
        }
        return cardBitmap
    }
}
