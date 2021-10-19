package com.shentu.paper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.micro.base.BaseFragment
import com.micro.mvp.IPresenter
import com.micro.utils.ArmsUtils
import com.shentu.paper.R
import com.shentu.paper.mvp.contract.RankContract
import kotlinx.android.synthetic.main.fragment_rank.*

class RankFragment : BaseFragment<IPresenter>(), RankContract.View {
    companion object {
        fun newInstance(): RankFragment {
            val fragment = RankFragment()
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //填充statusBar
        val lp = statusView.layoutParams as LinearLayout.LayoutParams
        lp.height = BarUtils.getStatusBarHeight()
        statusView.layoutParams = lp
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_rank, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ToastUtils.showShort(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }
}
