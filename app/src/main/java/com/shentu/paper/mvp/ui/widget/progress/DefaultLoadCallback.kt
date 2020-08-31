package com.shentu.paper.mvp.ui.widget.progress

import com.github.piasy.biv.loader.ImageLoader
import java.io.File

interface DefaultLoadCallback : ImageLoader.Callback {

    override fun onCacheHit(imageType: Int, image: File?) {

    }

    override fun onCacheMiss(imageType: Int, image: File?) {

    }

    override fun onFail(error: Exception?) {

    }

    override fun onFinish() {

    }

    override fun onProgress(progress: Int) {

    }

    override fun onStart() {

    }

    override fun onSuccess(image: File?) {

    }
}