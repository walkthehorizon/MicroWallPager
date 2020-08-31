package com.shentu.paper.mvp.ui.update

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.shentu.paper.BuildConfig
import com.shentu.paper.R
import com.shentu.paper.app.AppLifecycleImpl
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.RequestExecutor
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference

/*基于DownloadManager实现应用更新*/
class AppDownloadManager(activity: Activity) : LifecycleObserver {
    private val fileMimeType = "application/vnd.android.package-archive"

    private val weakReference: WeakReference<Activity> = WeakReference(activity)
    private val mDownloadManager: DownloadManager
    private val mDownLoadChangeObserver: DownloadChangeObserver
    private val mDownloadReceiver: DownloadReceiver
    private var mReqId: Long = 0
    private var mUpdateListener: OnUpdateListener? = null
    private lateinit var apkFile: File

    init {
        mDownloadManager = weakReference.get()?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        mDownLoadChangeObserver = DownloadChangeObserver(Handler())
        mDownloadReceiver = DownloadReceiver()

        //设置监听Uri.parse("content://downloads/my_downloads")
        weakReference.get()?.contentResolver?.registerContentObserver(Uri.parse("content://downloads/my_downloads"), true,
                mDownLoadChangeObserver)
        // 注册广播，监听APK是否下载完成
        weakReference.get()?.registerReceiver(mDownloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun setUpdateListener(mUpdateListener: OnUpdateListener) {
        this.mUpdateListener = mUpdateListener
    }

    fun downloadApk(apkUrl: String, appName: String, appVersionName: String) {
        Timber.e("appUrl:$apkUrl")
        //fileName
        val apkFileName = BuildConfig.APPLICATION_ID + "_" + appVersionName + ".apk"
        apkFile = File(PathUtils.getExternalDownloadsPath(), apkFileName)
        if (apkFile.exists()) {//如果当前需要下载的版本已存在，直接安装
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                checkPermissionInstall(apkFile)
            } else {
                installApk(apkFile)
            }
            return
        }
        val request = DownloadManager.Request(Uri.parse(apkUrl))
        //设置title
        request.setTitle(appName)
        // 设置描述
        request.setDescription(appVersionName)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkFileName)
        request.setMimeType(fileMimeType)
        mReqId = mDownloadManager.enqueue(request)
    }

    /**
     * 取消下载,删除已下载的文件
     */
    fun cancel() {
        mDownloadManager.remove(mReqId)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        weakReference.get()?.contentResolver?.unregisterContentObserver(mDownLoadChangeObserver)
        weakReference.get()?.unregisterReceiver(mDownloadReceiver)
    }

    private fun updateView() {
        val bytesAndStatus = intArrayOf(0, 0, 0)
        val query = DownloadManager.Query().setFilterById(mReqId)
        var c: Cursor? = null
        try {
            c = mDownloadManager.query(query)
            if (c != null && c.moveToFirst()) {
                //已经下载的字节数
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                //总需下载的字节数
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                //状态所在的列索引
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
                val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                Timber.e("status $status   reason:$reason")

            }
        } finally {
            c?.close()
        }

        if (mUpdateListener != null) {
            mUpdateListener!!.update(bytesAndStatus[0], bytesAndStatus[1])
        }

        Timber.i("下载进度：${bytesAndStatus[0]}/${bytesAndStatus[1]}")
    }

    internal inner class DownloadChangeObserver
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run [.onChange] on, or null if none.
     */
    (handler: Handler) : ContentObserver(handler) {

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            updateView()
        }
    }

    internal inner class DownloadReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Timber.e("下载完成")
            // 兼容Android 8.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AndPermission.with(context)
                        .install()
                        .file(apkFile)
                        .rationale { _, _, executor -> showInstallRequestDialog(executor) }
                        .onGranted { installApk(context, intent) }
                        .onDenied { ToastUtils.showShort("权限获取失败，取消升级") }
            } else {
                installApk(context, intent)
            }
        }
    }

    /**
     * 安装已存在的apk文件
     */
    private fun installApk(apkFile: File) {
        val uri: Uri
        val intentInstall = Intent()
        intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intentInstall.action = Intent.ACTION_VIEW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = AndPermission.getFileUri(weakReference.get(), apkFile)
            intentInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(apkFile)
        }
        intentInstall.setDataAndType(uri, "application/vnd.android.package-archive")
        weakReference.get()?.startActivity(intentInstall)
    }

    /**
     * 安装DownLoadManager下载到的apk文件
     *
     * @param context
     * @param intent
     */
    private fun installApk(context: Context, intent: Intent) {
        val completeDownLoadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

        Timber.e("收到广播")
        val uri: Uri
        val intentInstall = Intent()
        intentInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intentInstall.action = Intent.ACTION_VIEW

        if (completeDownLoadId == mReqId) {
            when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> // 6.0以下
                    uri = mDownloadManager.getUriForDownloadedFile(completeDownLoadId)
                Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> { // 6.0 - 7.0
                    val apkFile = queryDownloadedApk(context, completeDownLoadId)
                    uri = Uri.fromFile(apkFile)
                }
                else -> { // Android 7.0 以上
                    uri = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".fileProvider", apkFile)
                    intentInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }

            // 安装应用
            Timber.e("下载完成了")

            intentInstall.setDataAndType(uri, "application/vnd.android.package-archive")
            context.startActivity(intentInstall)
        }
    }

    //通过downLoadId查询下载的apk，解决6.0以后安装的问题
    private fun queryDownloadedApk(context: Context, downloadId: Long): File? {
        var targetApkFile: File? = null
        val downloader = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        if (downloadId != -1L) {
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
            val cur = downloader.query(query)
            if (cur != null) {
                if (cur.moveToFirst()) {
                    val uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApkFile = File(Uri.parse(uriString).path!!)
                    }
                }
                cur.close()
            }
        }
        return targetApkFile
    }

    private fun checkPermissionInstall(apkFile: File) {
        AndPermission.with(weakReference.get())
                .install()
                .file(apkFile)
                .rationale { _, _, executor -> showInstallRequestDialog(executor) }
                .onDenied { ToastUtils.showShort("授权获取失败，无法升级应用") }
                .start()
    }

    private fun showInstallRequestDialog(executor: RequestExecutor) {
        weakReference.get()?.let {
            MaterialDialog(it).show {
                title(text = "权限请求")
                message(text = "为了正常升级" + AppLifecycleImpl.instance.resources.getString(
                        R.string.app_name) + "APP，请点击设置按钮，允许安装未知来源应用")
                positiveButton(text = "设置") {
                    executor.execute()
                }
                cancelable(false)
            }
        }
    }

    interface OnUpdateListener {
        fun update(currentByte: Int, totalByte: Int)
    }
}