package com.shentu.wallpager.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.shentu.wallpager.mvp.contract.HotPagerContract;
import com.shentu.wallpager.mvp.model.entity.WallPager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


@FragmentScope
public class HotPagerModel extends BaseModel implements HotPagerContract.Model {

    private List<WallPager> wallPagers;

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public HotPagerModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        initPages();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    private void initPages() {
        wallPagers = new ArrayList<>();
        wallPagers.add(new WallPager("立春", "东风解冻，辈虫始振，鱼险发冰", "http://wallpager-1251812446.file.myqcloud.com/image/800002557.jpg"));
        wallPagers.add(new WallPager("雨水", "獭祭鱼;鸿雁来;草木霸动", "http://wallpager-1251812446.file.myqcloud.com/image/800002558.jpg"));
        wallPagers.add(new WallPager("惊蛰", "桃始华;黄鹂鸣;鹰化为鸠", "http://wallpager-1251812446.file.myqcloud.com/image/800002559.jpg"));
        wallPagers.add(new WallPager("春分", "元鸟至;雷乃发声;始电", "http://wallpager-1251812446.file.myqcloud.com/image/800002560.jpg"));
        wallPagers.add(new WallPager("清明", "桐始华;田鼠化为駕;虹始见", "http://wallpager-1251812446.file.myqcloud.com/image/800002561.jpg"));
        wallPagers.add(new WallPager("谷雨", "萍始生;鸣鸠拂其羽;戴任降于桑", "http://wallpager-1251812446.file.myqcloud.com/image/800002562.jpg"));
        wallPagers.add(new WallPager("立春", "东风解冻，辈虫始振，鱼险发冰", "http://wallpager-1251812446.file.myqcloud.com/image/800002557.jpg"));
        wallPagers.add(new WallPager("雨水", "獭祭鱼;鸿雁来;草木霸动", "http://wallpager-1251812446.file.myqcloud.com/image/800002558.jpg"));
        wallPagers.add(new WallPager("惊蛰", "桃始华;黄鹂鸣;鹰化为鸠", "http://wallpager-1251812446.file.myqcloud.com/image/800002559.jpg"));
        wallPagers.add(new WallPager("春分", "元鸟至;雷乃发声;始电", "http://wallpager-1251812446.file.myqcloud.com/image/800002560.jpg"));
        wallPagers.add(new WallPager("清明", "桐始华;田鼠化为駕;虹始见", "http://wallpager-1251812446.file.myqcloud.com/image/800002561.jpg"));
        wallPagers.add(new WallPager("谷雨", "萍始生;鸣鸠拂其羽;戴任降于桑", "http://wallpager-1251812446.file.myqcloud.com/image/800002562.jpg"));
    }

    @Override
    public Observable<List<WallPager>> getWallPageList() {
        return Observable.create(new ObservableOnSubscribe<List<WallPager>>() {
            @Override
            public void subscribe(ObservableEmitter<List<WallPager>> emitter) throws Exception {
                emitter.onNext(wallPagers);
            }
        });
    }
}