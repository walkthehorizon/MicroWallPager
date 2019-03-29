package com.shentu.wallpaper.mvp.ui.adapter;

import android.graphics.Color;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.florent37.glidepalette.GlidePalette;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.app.GlideArms;
import com.shentu.wallpaper.app.utils.PicUtils;
import com.shentu.wallpaper.app.utils.ShareUtils;
import com.shentu.wallpaper.model.entity.Subject;
import com.shentu.wallpaper.mvp.ui.activity.PictureBrowserActivity;

import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class HotAdapter extends BaseMultiItemQuickAdapter<Subject, BaseViewHolder> {

    public HotAdapter(List<Subject> data) {
        super(data);
        addItemType(Subject.ITEM_VIEW_1, R.layout.app_item_hot_page_1);
        addItemType(Subject.ITEM_VIEW_2, R.layout.app_item_hot_page_2);
        addItemType(Subject.ITEM_VIEW_3, R.layout.app_item_hot_page_3);

        this.setOnItemClickListener((adapter, view, position) -> {
            Subject subject = (Subject) adapter.getData().get(position);
            ARouter.getInstance()
                    .build("/picture/browser/activity")
                    .withInt(PictureBrowserActivity.SUBJECT_ID, subject.id)
                    .navigation();
        });

        this.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.tv_share) {
                ShareUtils.getInstance().showShare(this.mContext);
            }
            if (view.getId() == R.id.tv_comment) {
                ToastUtils.showShort("评论");
            }
            if (view.getId() == R.id.tv_support) {
                ToastUtils.showShort("点赞");
            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, Subject item) {
        helper.getView(R.id.tv_content).setVisibility(View.GONE);
        GlideArms.with(helper.itemView.getContext())
                .load(item.cover)
                .placeholder(R.drawable.default_head)
                .circleCrop()
                .into((ImageView) helper.getView(R.id.iv_avatar));
        helper.setText(R.id.tv_user_name, item.name);
        helper.setText(R.id.tv_content, item.name + "：" + item.description);
        helper.getView(R.id.tv_support).setSelected(false);
        if (item.getItemType() == Subject.ITEM_VIEW_1) {
            GlideArms.with(helper.itemView.getContext())
                    .load(item.cover)
                    .listener(GlidePalette.with(item.cover)
                            .use(GlidePalette.Profile.VIBRANT_LIGHT)
                            .intoCallBack(palette -> ((CardView) helper.getView(R.id.cardView)).setCardBackgroundColor(Objects.requireNonNull(palette).getLightMutedColor(Color.LTGRAY))))
                    .transforms(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(5)))
                    .transition(withCrossFade())
                    .into((ImageView) helper.getView(R.id.iv_cover));
        }

        if (item.getItemType() == Subject.ITEM_VIEW_2) {
            GlideArms.with(helper.itemView.getContext())
                    .load(item.cover)
                    .listener(GlidePalette.with(item.cover)
                            .use(GlidePalette.Profile.MUTED_LIGHT)
                            .intoCallBack(palette -> ((CardView) helper.getView(R.id.cardView1)).setCardBackgroundColor(Objects.requireNonNull(palette).getLightMutedColor(Color.LTGRAY))))
                    .transforms(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(5)))
                    .transition(withCrossFade())
                    .into((ImageView) helper.getView(R.id.iv_1));
            GlideArms.with(helper.itemView.getContext())
                    .load(item.cover_1)
                    .listener(GlidePalette.with(item.cover_1)
                            .use(GlidePalette.Profile.MUTED)
                            .intoCallBack(palette -> ((CardView) helper.getView(R.id.cardView2)).setCardBackgroundColor(Objects.requireNonNull(palette).getLightMutedColor(Color.LTGRAY))))
                    .transforms(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(5)))
                    .into((ImageView) helper.getView(R.id.iv_2));
            GlideArms.with(helper.itemView.getContext())
                    .load(item.cover_2)
                    .listener(GlidePalette.with(item.cover_2)
                            .use(GlidePalette.Profile.VIBRANT)
                            .intoCallBack(palette -> ((CardView) helper.getView(R.id.cardView3)).setCardBackgroundColor(Objects.requireNonNull(palette).getLightMutedColor(Color.LTGRAY))))
                    .transforms(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(5)))
                    .into((ImageView) helper.getView(R.id.iv_3));
        }

        if (item.getItemType() == Subject.ITEM_VIEW_3) {
            MultiTransformation multi = new MultiTransformation<>(new RoundedCornersTransformation(ConvertUtils.dp2px(5),
                    0, RoundedCornersTransformation.CornerType.ALL), new CenterCrop());
            GlideArms.with(helper.itemView.getContext())
                    .load(PicUtils.getInstance().getMM131GlideUrl(item.cover))
                    .transforms(multi)
                    .into((ImageView) helper.getView(R.id.iv_cover));
        }
        helper.addOnClickListener(R.id.tv_share)
                .addOnClickListener(R.id.tv_support);
    }
}
