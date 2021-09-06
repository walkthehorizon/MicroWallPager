package com.shentu.paper.mvp.ui.activity

import android.os.Bundle
import android.view.View
import com.google.android.material.button.MaterialButton
import com.shentu.paper.R
import com.shentu.paper.app.base.BaseBindingActivity
import com.shentu.paper.databinding.ActivityTestBinding
import timber.log.Timber

class TestMMKVActivity : BaseBindingActivity<ActivityTestBinding>(), View.OnClickListener {
    private lateinit var mbPlay: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mbPlay.setOnClickListener(this)
    }

    private fun testClick() {
        Timber.e("efsdgsdgsdgsd")
    }

    override fun onClick(v: View?) {
        testClick()
    }
}