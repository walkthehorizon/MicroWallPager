
package com.jess.arms.integration.lifecycle;

import android.app.Activity;

import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;

/**
 * ================================================
 * 让 {@link Activity} 实现此接口,即可正常使用 {@link RxLifecycle}
 *
 * Created by JessYan on 26/08/2017 17:14
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public interface ActivityLifecycleable extends Lifecycleable<ActivityEvent> {
}
