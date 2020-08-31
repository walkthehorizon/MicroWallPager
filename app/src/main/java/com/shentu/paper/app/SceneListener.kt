package com.shentu.paper.app;

import android.app.Activity
import com.mob.moblink.RestoreSceneListener
import com.mob.moblink.Scene
import com.shentu.paper.mvp.ui.browser.PictureBrowserActivity
import timber.log.Timber

internal class SceneListener :RestoreSceneListener {
    override fun willRestoreScene(scene: Scene): Class<out Activity> {
        return PictureBrowserActivity::class.java
    }
    override fun notFoundScene(scene: Scene) {
        Timber.e("未找到处理者")
    }
    override fun completeRestore(scene: Scene) {
        Timber.e("找到了处理者")
    }
}