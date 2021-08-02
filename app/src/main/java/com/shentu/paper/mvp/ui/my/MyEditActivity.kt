package com.shentu.paper.mvp.ui.my

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.micro.base.BaseActivity
import com.micro.utils.ArmsUtils
import com.shentu.paper.R
import com.shentu.paper.app.GlideApp
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.mvp.contract.MyEditContract
import com.shentu.paper.mvp.presenter.MyEditPresenter
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_my_edit.*
import java.io.File

@AndroidEntryPoint
class MyEditActivity : BaseActivity<MyEditPresenter>(), MyEditContract.View {

    companion object {
        private const val REQUEST_CODE = 23
        private const val CROP_CODE = 89
    }

    //裁剪输出路径
    private val cropImage = File(PathUtils.getExternalAppCachePath(), "avatar_crop.jpg")
    private val user: MicroUser = HkUserManager.user


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_my_edit
    }


    override fun initData(savedInstanceState: Bundle?) {
        refreshView()
        rlAvatar.setOnClickListener {
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .rationale { shouldRequest ->
                        MaterialDialog(this@MyEditActivity).show {
                            title(text = "提示")
                            message(text = "请允许获取存储权限以读取本地图片")
                            positiveButton(text = "好") { shouldRequest?.again(true) }
                        }
                    }
                    .callback(object : PermissionUtils.SimpleCallback {
                        override fun onGranted() {
                            openMatisse()
                        }

                        override fun onDenied() {
                            ToastUtils.showShort("权限被拒绝")
                        }
                    })
                    .request()
        }
        rivNickName.setOnClickListener {
            showNickNameDialog()
        }
        rivSex.setOnClickListener {
            showSexDialog()
        }
    }

    /**
     * scrollToTop
     * */
    override fun refreshView() {
        GlideApp.with(this)
                .load(user.avatar)
                .transform(CircleCrop())
                .error(R.drawable.default_head)
                .into(ivAvatar)
        rivNickName.setEndValue(user.nickname)
        rivSex.setEndValue(
                when (user.sex) {
                    1 -> "男"
                    2 -> "女"
                    else -> "保密"
                }
        )
    }

    private fun showNickNameDialog() {
        MaterialDialog(this).show {
            title(text = "昵称")
            input("新的昵称", prefill = HkUserManager.user.nickname
                    , maxLength = 6) { _, sequence ->
                HkUserManager.user.nickname = sequence.toString()
                mPresenter?.updateUser()
            }
        }
    }

    private fun showSexDialog() {
        MaterialDialog(this).show {
            title(text = "性别")
            positiveButton(text = "确认")
            listItemsSingleChoice(items = listOf("保密", "男", "女")
                    , initialSelection = HkUserManager.user.sex) { _, index, _ ->
                HkUserManager.user.sex = index
                mPresenter?.updateUser()
            }
        }
    }

    private fun openMatisse() {
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .maxSelectable(1)
                .capture(true)
                .captureStrategy(CaptureStrategy(true,
                        "com.shentu.wallpaper.fileprovider", null))
                .imageEngine(Glide4Engine())
                .forResult(REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE -> openPhotoCrop(Matisse.obtainResult(data)[0]
                        , Matisse.obtainPathResult(data)[0])
                else -> mPresenter?.uploadAvatar(cropImage.absolutePath)
            }
        }
    }

    private fun openPhotoCrop(imageUri: Uri, imagePath: String) {
        if (!cropImage.exists()) {
            cropImage.createNewFile()
        }
        val intent = Intent("com.android.camera.action.CROP")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 200)
        intent.putExtra("outputY", 200)
        intent.putExtra("scale", true)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(cropImage.absolutePath))
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("return-data", false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(imageUri, "image/*")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropImage))
        } else {
            intent.setDataAndType(Uri.fromFile(File(imagePath)), "image/*")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropImage))
        }
        startActivityForResult(intent, CROP_CODE)
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }
}
