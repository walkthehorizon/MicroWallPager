package com.shentu.wallpaper.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jess.arms.base.BaseFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.shentu.wallpaper.R
import com.shentu.wallpaper.di.component.DaggerCategoryPageComponent
import com.shentu.wallpaper.di.module.CategoryPageModule
import com.shentu.wallpaper.model.entity.Wallpaper
import com.shentu.wallpaper.mvp.contract.CategoryPageContract
import com.shentu.wallpaper.mvp.presenter.CategoryPagePresenter
import com.shentu.wallpaper.mvp.ui.adapter.CategoryPageAdapter
import kotlinx.android.synthetic.main.fragment_category_page.*


/**
 * 如果没presenter
 * 你可以这样写
 *
 * @FragmentScope(請注意命名空間) class NullObjectPresenterByFragment
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class CategoryPageFragment : BaseFragment<CategoryPagePresenter>(), CategoryPageContract.View {

    private var curPos: Int = 0
    private var curPage: Int = 0
    private var categoryId: Int = 0

    companion object {
        val CUR_PAGE = "cur_page"
        val CUR_POS = "cur_pos"
        val CATEGORY_ID = "category_id"
        fun newInstance(page: Int, pos: Int, categoryId: Int): CategoryPageFragment {
            val fragment = CategoryPageFragment()
            val args = Bundle()
            args.putInt(CUR_PAGE, page)
            args.putInt(CUR_POS, pos)
            args.putInt(CATEGORY_ID, categoryId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerCategoryPageComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .categoryPageModule(CategoryPageModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_category_page, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        curPos = arguments?.getInt(CUR_POS) ?: 0
        curPage = arguments?.getInt(CUR_PAGE) ?: 0
        categoryId = arguments?.getInt(CATEGORY_ID) ?: 0

//        mPresenter?.getCategoryList(categoryId, curPage, false)
    }


    override fun showCategoryPicture(wallpapers: MutableList<Wallpaper>) {
        if (viewPager.adapter == null) {
            viewPager.adapter = CategoryPageAdapter(curPos, wallpapers, childFragmentManager)
            viewPager.offscreenPageLimit = 2
            viewPager.currentItem = curPos
            viewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageSelected(position: Int) {
                    if (position == (viewPager.adapter as CategoryPageAdapter).data.size - 3) {
//                        mPresenter?.getCategoryList(categoryId, ++curPage, false)
                    }
                }
            })
        } else {
            (viewPager.adapter as CategoryPageAdapter).data.addAll(wallpapers)
            (viewPager.adapter as CategoryPageAdapter).notifyDataSetChanged()
        }
    }

    override fun setData(data: Any?) {

    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {

    }
}
