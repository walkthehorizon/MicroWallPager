package com.shentu.paper.mvp.ui.browser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.NavArgument
import androidx.navigation.findNavController
import com.github.piasy.biv.BigImageViewer
import com.mob.moblink.MobLink
import com.mob.moblink.Scene
import com.mob.moblink.SceneRestorable
import com.shentu.paper.R
import com.shentu.paper.app.base.BaseBindingActivity
import com.shentu.paper.databinding.ActivityPaperBrowserBinding
import com.shentu.paper.mvp.ui.browser.PaperBrowserFragment.Companion.KEY_PAPER_SOURCE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaperBrowserActivity : BaseBindingActivity<ActivityPaperBrowserBinding>(), SceneRestorable {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_FullScreen)
        window.enterTransition = Fade()
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        nav2Picture(intent.getSerializableExtra(KEY_PAPER_SOURCE) as PaperSource)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    private fun nav2Picture(source: PaperSource) {
        supportFragmentManager.beginTransaction().replace(
            R.id.container,PaperBrowserFragment.newInstance(source),PaperBrowserFragment::class.java.simpleName)
            .commit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        MobLink.updateNewIntent(getIntent(), this)
    }

    override fun onReturnSceneData(scene: Scene) {
        val paperId = scene.params["id"] as Long
        nav2Picture(SourcePaper(paperId))
    }

    override fun onDestroy() {
        super.onDestroy()
        BigImageViewer.imageLoader().cancelAll()
    }

    companion object {

        fun open(context: Context, source: PaperSource , compat: ActivityOptionsCompat? = null) {
            val intent = Intent(context, PaperBrowserActivity::class.java)
            intent.putExtra(KEY_PAPER_SOURCE, source)
            ActivityCompat.startActivity(context,intent,compat?.toBundle())
        }
    }
}