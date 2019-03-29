package com.shentu.wallpaper.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ScreenUtils;
import com.shentu.wallpaper.R;
import com.shentu.wallpaper.mvp.ui.fragment.CategoryPageFragment;

import timber.log.Timber;

@Route(path = "/category/page/activity")
public class CategoryPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("Time:"+System.currentTimeMillis());
        ScreenUtils.setFullScreen(this);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_category_page);
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

    public static void open(Context context,int categoryId, int page, int pos , ActivityOptionsCompat options){
        Intent intent = new Intent(context,CategoryPageActivity.class);
        intent.putExtra(CategoryPageFragment.Companion.getCATEGORY_ID(),categoryId);
        intent.putExtra(CategoryPageFragment.Companion.getCUR_PAGE(),page);
        intent.putExtra(CategoryPageFragment.Companion.getCUR_POS(),pos);
        context.startActivity(intent,options.toBundle());
    }
}
