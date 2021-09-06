package com.shentu.paper.mvp.ui.browser

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shentu.paper.app.base.BaseBindingFragment
import com.shentu.paper.databinding.FragmentPictureBrowserBinding
import timber.log.Timber

class PictureBrowserFragment : BaseBindingFragment<FragmentPictureBrowserBinding>() {

    val source : PaperSource by lazy {
        findNavController().graph.arguments[KEY_PAPER_SOURCE]!!.defaultValue as PaperSource
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.e("args.pictureArgs %s", source.toString())


    }

    companion object {

        const val KEY_PAPER_SOURCE = "KEY_PAPER_SOURCE"

//        fun newInstance(source: PaperSource): PictureBrowserFragment {
//            val args = Bundle()
//            args.putSerializable(KEY_PAPER_SOURCE, source)
//            val fragment = PictureBrowserFragment()
//            fragment.arguments = args
//            return fragment
//        }
    }

}