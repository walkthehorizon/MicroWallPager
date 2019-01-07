package com.shentu.wallpaper.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.shentu.wallpaper.model.entity.BasePageResponse;
import com.shentu.wallpaper.model.entity.BaseResponse;
import com.shentu.wallpaper.model.entity.Subject;

import java.util.List;

import io.reactivex.Observable;


public interface HotPagerContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        void showHotSubject(List<Subject> subjects , boolean clear);//展示推荐壁纸

        void showFilterPop();
    }

    interface Model extends IModel {
        Observable<BaseResponse<BasePageResponse<Subject>>> getSubjects(int subjectType, boolean clear);
    }
}
