package com.shentu.wallpaper.app;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.jess.arms.utils.ArmsUtils;
import com.shentu.wallpaper.model.entity.User;
import com.shentu.wallpaper.mvp.ui.activity.LoginActivity;


public class HkUserManager {
    private String USER_UID = "user_uid";
    private String USER_AVATAR = "user_avatar";
    private String USER_NICKNAME = "user_nickname";
    private String USER_PHONE = "user_phone";
    private String USER_EMAIL = "user_email";
    private String USER_SIGNATURE = "user_signature";
    private String USER_DATE_JOINED = "user_date_joined";
    private String USER_LAST_LOGIN = "user_last_login";
    public User user;

    private static final class SingletonHolder{
        private static final HkUserManager INSTANCE = new HkUserManager();
    }

    HkUserManager() {
        user = new User();
        user.id = SPUtils.getInstance().getString(USER_UID,"");
        user.nickname = SPUtils.getInstance().getString(USER_NICKNAME,"");
        user.avatar = SPUtils.getInstance().getString(USER_AVATAR,"");
        user.email = SPUtils.getInstance().getString(USER_EMAIL,"");
        user.phone = SPUtils.getInstance().getString(USER_PHONE,"");
        user.signature = SPUtils.getInstance().getString(USER_SIGNATURE,"");
        user.date_joined = SPUtils.getInstance().getString(USER_DATE_JOINED,"");
        user.last_login = SPUtils.getInstance().getString(USER_LAST_LOGIN,"");
    }

    public static HkUserManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 参考python,存储变化前请先更新user
     * */
    public void save(){
        SPUtils.getInstance().put(USER_UID, user.id);
        SPUtils.getInstance().put(USER_NICKNAME, user.nickname);
        SPUtils.getInstance().put(USER_AVATAR, user.avatar);
        SPUtils.getInstance().put(USER_EMAIL, user.email);
        SPUtils.getInstance().put(USER_PHONE, user.phone);
        SPUtils.getInstance().put(USER_SIGNATURE, user.signature);
        SPUtils.getInstance().put(USER_DATE_JOINED, user.date_joined);
        SPUtils.getInstance().put(USER_LAST_LOGIN, user.last_login);
    }

    /**
     * 移除所有Sp数据，需要持久化保存不被删除的请使用Cache
     * */
    public void clear(){
        SPUtils.getInstance().clear();
    }

    public boolean isLogined(){
        return !TextUtils.isEmpty(user.id);
    }

    public void checkLogin(Context context){
        if(!isLogined()){
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }
}
