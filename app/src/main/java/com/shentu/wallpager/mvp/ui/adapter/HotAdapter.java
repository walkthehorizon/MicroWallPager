package com.shentu.wallpager.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.shentu.wallpager.R;
import com.shentu.wallpager.mvp.model.entity.WallPager;

import java.util.List;

public class HotAdapter extends BaseQuickAdapter<WallPager , BaseViewHolder>{
    public HotAdapter(@Nullable List<WallPager> data) {
        super(R.layout.app_item_hot_pager, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WallPager item) {
        GlideArms.with(helper.itemView.getContext())
                .load(item.url)
                .centerCrop()
                .into((ImageView) helper.getView(R.id.iv_cover));
    }
}
