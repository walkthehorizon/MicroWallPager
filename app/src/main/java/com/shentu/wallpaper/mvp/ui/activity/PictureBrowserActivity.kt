package com.shentu.wallpaper.mvp.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ScreenUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.mvp.ui.fragment.PictureBrowserFragment

@Route(path = "/picture/browser/activity")
class PictureBrowserActivity : AppCompatActivity() {
    companion object {
        const val SUBJECT_ID: String = "subject_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenUtils.setFullScreen(this)
        setContentView(R.layout.activity_picture_browser)

        val subjectId: Int = intent.getIntExtra(SUBJECT_ID, -1)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, PictureBrowserFragment.newInstance(subjectId))
                .commit()
    }
}