package com.shentu.wallpaper.mvp.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.mvp.ui.fragment.CategoryListFragment
import retrofit2.http.Path


@Route(path = "/category/list/activity")
class CategoryListActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        BarUtils.setStatusBarAlpha(this)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        ScreenUtils.setFullScreen(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        val id:Int = intent.getIntExtra(CategoryListFragment.CATEGORY_ID,0)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, CategoryListFragment.newInstance(id))
                .commit()
    }

}