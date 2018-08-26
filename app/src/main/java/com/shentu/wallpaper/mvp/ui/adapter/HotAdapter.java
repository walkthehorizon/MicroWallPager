package com.shentu.wallpaper.mvp.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.app.utils.HkUtils;
import com.shentu.wallpaper.app.utils.PicUtils;
import com.shentu.wallpaper.app.utils.ShareUtils;
import com.shentu.wallpaper.model.entity.Subject;
import com.shentu.wallpaper.mvp.ui.activity.PictureBrowserActivity;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class HotAdapter extends BaseMultiItemQuickAdapter<Subject, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
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

        GlideArms.with(helper.itemView.getContext())
                .load(item.owner.avatar)
                .placeholder(R.drawable.default_head)
                .circleCrop()
                .into((ImageView) helper.getView(R.id.iv_avatar));
        helper.setText(R.id.tv_user_name, item.owner.nickname);
        helper.setText(R.id.tv_content, item.name + "：" + item.description);
        helper.getView(R.id.tv_support).setSelected(item.supported);
        if (item.getItemType() == Subject.ITEM_VIEW_1) {
            Transformation<Bitmap> transformation = new RoundedCornersTransformation(ConvertUtils.dp2px(5),
                    0, RoundedCornersTransformation.CornerType.ALL);
            GlideArms.with(helper.itemView.getContext())
                    .load(item.cover)
                    .transform(transformation)
                    .into((ImageView) helper.getView(R.id.iv_cover));
        }

        if (item.getItemType() == Subject.ITEM_VIEW_2) {
            Transformation<Bitmap> transformation = new RoundedCornersTransformation(ConvertUtils.dp2px(5),
                    0, RoundedCornersTransformation.CornerType.ALL);
            GlideArms.with(helper.itemView.getContext())
                    .load(PicUtils.getInstance().buildtl640(item.cover))
                    .transform(transformation)
                    .into((ImageView) helper.getView(R.id.iv_1));
            GlideArms.with(helper.itemView.getContext())
                    .load(PicUtils.getInstance().buildtl200(item.cover.replace("type_2/1","type_2/2")))
                    .transform(transformation)
                    .into((ImageView) helper.getView(R.id.iv_2));
            GlideArms.with(helper.itemView.getContext())
                    .load(PicUtils.getInstance().buildtl200(item.cover.replace("type_2/1","type_2/3")))
                    .transform(transformation)
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
