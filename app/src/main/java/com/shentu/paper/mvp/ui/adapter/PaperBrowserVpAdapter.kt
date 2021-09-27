package com.shentu.paper.mvp.ui.adapter

import android.util.SparseArray
import android.util.SparseIntArray
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.mvp.ui.fragment.PictureFragment

class PaperBrowserVpAdapter(
    private val parent: Fragment,
    private val papers: List<Wallpaper>,
) :
    FragmentStateAdapter(parent) {

    private val fragments: SparseArray<Fragment> = SparseArray()

    override fun getItemCount(): Int {
        return papers.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = PictureFragment.newInstance(papers[position], position)
        if (parent is PictureFragment.Callback) {
            fragment.setCallback(parent as PictureFragment.Callback)
        }
        fragments.put(position, fragment)
        return fragment
    }

    override fun onViewDetachedFromWindow(holder: FragmentViewHolder) {
        super.onViewDetachedFromWindow(holder)
        fragments.remove(holder.bindingAdapterPosition)
    }

    fun getFragment(position: Int): Fragment? {
        return fragments[position]
    }
}