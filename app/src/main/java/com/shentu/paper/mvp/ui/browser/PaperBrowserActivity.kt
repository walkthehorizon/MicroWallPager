package com.shentu.paper.mvp.ui.browser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.shentu.paper.R
import com.shentu.paper.app.base.BaseBindingActivity
import com.shentu.paper.databinding.ActivityPaperBrowserBinding
import com.shentu.paper.databinding.ActivityPictureBrowserBinding
import com.shentu.paper.mvp.ui.browser.PictureBrowserFragment.Companion.KEY_PAPER_SOURCE

class PaperBrowserActivity : BaseBindingActivity<ActivityPaperBrowserBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PictureBrowserFragmentArgs

        val navController = findNavController(R.id.nav_host_fragment)
        val navGraph = navController.graph
        navGraph.addArgument(
            KEY_PAPER_SOURCE,
            NavArgument.Builder().setDefaultValue(Recommend(3)).build()
        )
    }

    companion object {

        fun open(context: Context, source: PaperSource) {
            val intent = Intent(context, PaperBrowserActivity::class.java)
            intent.putExtra(KEY_PAPER_SOURCE, source)
            context.startActivity(intent)
        }
    }
}