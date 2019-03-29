package com.shentu.wallpaper.mvp.ui.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.mvp.ui.fragment.LoginFragment

class LoginActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
//        ScreenUtils.setFullScreen(this)
        BarUtils.setStatusBarColor(this,Color.TRANSPARENT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container,LoginFragment.newInstance())
                .commit()
    }
}