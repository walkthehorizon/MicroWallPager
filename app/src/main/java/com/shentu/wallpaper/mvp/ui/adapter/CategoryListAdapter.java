package com.shentu.wallpaper.mvp.ui.adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.app.GlideArms;
import com.shentu.wallpaper.app.utils.PicUtils;
import com.shentu.wallpaper.model.entity.Wallpaper;

import java.util.List;

public class CategoryListAdapter extends BaseQuickAdapter<Wallpaper, BaseViewHolder> {
    public CategoryListAdapter(@Nullable List<Wallpaper> data , int categoryId) {
        super(R.layout.item_rv_category_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Wallpaper item) {
        GlideArms.with(helper.itemView.getContext())
                .load(PicUtils.getInstance().getMM131GlideUrl(item.getUrl()))
                .placeholder(R.drawable.default_cover_vertical)
                .error(R.drawable.default_cover_vertical)
                .into((ImageView) helper.getView(R.id.iv_small));
    }
}
