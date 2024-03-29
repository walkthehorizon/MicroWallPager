package com.shentu.paper.mvp.ui.fragment

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.micro.base.BaseActivity
import com.micro.mvp.IPresenter
import com.shentu.paper.R
import kotlinx.android.synthetic.main.activity_category_list.*


@Route(path = "/activity/category/detail")
class CategoryListActivity : BaseActivity<IPresenter>() {
    @JvmField
    @Autowired
    var title = ""
    @JvmField
    @Autowired
    var cid = 0

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_category_list
    }

    override fun initData(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        toolbar.setTitle(title)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, CategoryListFragment.newInstance(cid),CategoryListFragment::class.simpleName)
                .commit()
    }

    companion object {
        fun open(cid: Int, title: String) {
            ARouter.getInstance()
                    .build("/activity/category/detail")
                    .withInt("cid", cid)
                    .withString("title", title)
                    .navigation()
        }
    }
}