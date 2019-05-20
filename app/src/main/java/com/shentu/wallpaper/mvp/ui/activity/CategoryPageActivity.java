package com.shentu.wallpaper.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ScreenUtils;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.mvp.ui.fragment.CategoryPageFragment;

@Route(path = "/category/page/activity")
public class CategoryPageActivity extends BaseActivity {

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        ScreenUtils.setFullScreen(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_category_page;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        supportPostponeEnterTransition();//延缓执行共享元素动画
        int curPos = getIntent().getIntExtra(CategoryPageFragment.Companion.getCUR_POS(), 0);
        int curPage = getIntent().getIntExtra(CategoryPageFragment.Companion.getCUR_PAGE(), 0);
        int categoryId = getIntent().getIntExtra(CategoryPageFragment.Companion.getCATEGORY_ID(), 0);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container
                        , CategoryPageFragment.Companion.newInstance(curPage, curPos, categoryId)
                        , CategoryPageFragment.class.getSimpleName())
                .commit();
    }

    public static void open(Context context, int categoryId, int page, int pos, ActivityOptionsCompat options) {
        Intent intent = new Intent(context,CategoryPageActivity.class);
        intent.putExtra(CategoryPageFragment.Companion.getCATEGORY_ID(),categoryId);
        intent.putExtra(CategoryPageFragment.Companion.getCUR_PAGE(),page);
        intent.putExtra(CategoryPageFragment.Companion.getCUR_POS(),pos);
        context.startActivity(intent,options.toBundle());
    }
}
