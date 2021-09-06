package com.shentu.paper.mvp.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.paging.PagingDataAdapter
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseViewHolder
import com.github.florent37.glidepalette.BitmapPalette
import com.github.florent37.glidepalette.GlidePalette
import com.shentu.paper.R
import com.shentu.paper.app.GlideApp
import com.shentu.paper.app.utils.HkUtils
import com.shentu.paper.model.entity.Wallpaper
import java.util.*

class RecommendNewAdapter : PagingDataAdapter<Wallpaper, BaseViewHolder>(object : DiffUtil.ItemCallback<Wallpaper>() {
        override fun areItemsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Wallpaper, newItem: Wallpaper): Boolean {
            if(oldItem.collected != oldItem.collected){
                return false
            }
            return oldItem == newItem
        } }) {

    private var listener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_recommend, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val cardView: CardView = holder.getView(R.id.cardView)
        val ivPicture: ImageView = holder.getView(R.id.ivPicture)
        val item = getItem(position)!!
        GlideApp.with(holder.itemView.context)
            .load(HkUtils.instance.get2x2Image(item.url))
            .listener(
                GlidePalette.with(item.url)
                    .use(BitmapPalette.Profile.MUTED_LIGHT)
                    .intoCallBack { palette ->
                        (cardView).setCardBackgroundColor(
                            Objects.requireNonNull<Palette>(palette)
                                .getLightVibrantColor(Color.WHITE)
                        )
                    })
            .transform(
                MultiTransformation(
                    CenterCrop(),
                    RoundedCorners(ConvertUtils.dp2px(5f))
                )
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivPicture)
        holder.setText(R.id.tvLikeNum, item.collectNum.toString())

        holder.itemView.setOnClickListener {
            listener?.onClick(this, holder.itemView, position)
        }
    }

    interface OnClickListener {
        fun onClick(adapter: RecommendNewAdapter, view: View, position: Int)
    }

//    /**
//     * 重写以实现瀑布流布局时无缝嵌入head
//     * */
//    override fun setFullSpan(holder: RecyclerView.ViewHolder?) {
//        if (holder!!.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
//            val params: StaggeredGridLayoutManager.LayoutParams =
//                holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
//            params.isFullSpan = false
//        }
//    }

}
