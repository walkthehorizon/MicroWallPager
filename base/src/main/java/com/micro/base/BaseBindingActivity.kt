package com.micro.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import inflateBindingWithGeneric
import org.greenrobot.eventbus.EventBus

abstract class BaseBindingActivity<VB : ViewBinding> : AppCompatActivity() {
    protected lateinit var binding: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
        binding = inflateBindingWithGeneric<VB>(layoutInflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    fun useEventBus(): Boolean = false
}