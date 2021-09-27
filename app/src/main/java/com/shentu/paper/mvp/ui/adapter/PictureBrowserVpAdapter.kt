//package com.shentu.paper.mvp.ui.adapter
//
//
//import android.annotation.SuppressLint
//import android.util.SparseArray
//import android.view.ViewGroup
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.FragmentStatePagerAdapter
//import com.shentu.paper.model.entity.Wallpaper
//import com.shentu.paper.mvp.ui.fragment.PictureFragment
//
//@SuppressLint("WrongConstant")
//class PictureBrowserVpAdapter(fm: FragmentManager, private val wallpapers: List<Wallpaper>,
//                              val callback: PictureFragment.Callback) :
//        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//    private val fragments: SparseArray<PictureFragment> = SparseArray()
//
//    override fun getItem(pos: Int): androidx.fragment.app.Fragment {
//        fragments.put(pos, PictureFragment.newInstance(wallpapers[pos], pos))
//        fragments[pos].setCallback(callback)
//        return fragments[pos]
//    }
//
//    override fun getCount(): Int {
//        return wallpapers.size
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
//        fragments[position].setCallback(null)
//        fragments.remove(position)
//    }
//
//    fun getFragment(current: Int): PictureFragment {
//        return fragments.get(current)
//    }
//}