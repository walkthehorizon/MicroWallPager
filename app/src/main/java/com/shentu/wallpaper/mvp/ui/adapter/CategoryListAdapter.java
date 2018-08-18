package com.shentu.wallpaper.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.app.utils.HkUtils;
import com.shentu.wallpaper.app.utils.PicUtils;
import com.shentu.wallpaper.model.api.service.MicroService;
import com.shentu.wallpaper.model.entity.Category;
import com.shentu.wallpaper.model.entity.Wallpaper;
import com.shentu.wallpaper.mvp.ui.fragment.CategoryPageFragment;

import java.util.List;

public class CategoryListAdapter extends BaseQuickAdapter<Wallpaper, BaseViewHolder> {
    public CategoryListAdapter(@Nullable List<Wallpaper> data , int categoryId) {
        super(R.layout.item_rv_category_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Wallpaper item) {
        GlideArms.with(helper.itemView.getContext())
                .load(PicUtils.getInstance().getMM131GlideUrl(item.small_url))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_cover_vertical)
                .error(R.drawable.default_cover_vertical)
                .into((ImageView) helper.getView(R.id.iv_small));
    }
}
