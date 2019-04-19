package com.shentu.wallpaper.mvp.ui.activity

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ScreenUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.mvp.ui.fragment.CategoryDetailFragment


@Route(path = "activity/category/detail")
class CategoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        BarUtils.setStatusBarAlpha(this)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        ScreenUtils.setFullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        val id: Int = intent.getIntExtra(CategoryDetailFragment.CATEGORY_ID, 0)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, CategoryDetailFragment.newInstance(id))
                .commit()
    }

}