package com.shentu.wallpaper.app;

import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.SPUtils;
import com.shentu.wallpaper.model.entity.MicroUser;
import com.shentu.wallpaper.mvp.ui.login.LoginActivity;


public class HkUserManager {
    private String USER_UID = "user_uid";
    private String USER_AVATAR = "user_avatar";
    private String USER_NICKNAME = "user_nickname";
    private String USER_PHONE = "user_phone";
    private String USER_EMAIL = "user_email";
    private String USER_SIGNATURE = "user_signature";
    private String USER_DATE_JOINED = "user_date_joined";
    private String USER_LAST_LOGIN = "user_last_login";
    private String USER_SEX = "user_sex";
    public MicroUser user;

    private static final class SingletonHolder {
        private static final HkUserManager INSTANCE = new HkUserManager();
    }

    private HkUserManager() {
        if (SPUtils.getInstance().getInt(USER_UID, -1) == -1) {
            return;
        }
        user = new MicroUser();
        user.uid = SPUtils.getInstance().getInt(USER_UID, -1);
        user.nickname = SPUtils.getInstance().getString(USER_NICKNAME, "");
        user.avatar = SPUtils.getInstance().getString(USER_AVATAR, "");
        user.email = SPUtils.getInstance().getString(USER_EMAIL, "");
        user.phone = SPUtils.getInstance().getString(USER_PHONE, "");
        user.signature = SPUtils.getInstance().getString(USER_SIGNATURE, "");
        user.date_joined = SPUtils.getInstance().getString(USER_DATE_JOINED, "");
        user.last_login = SPUtils.getInstance().getString(USER_LAST_LOGIN, "");
        user.sex = SPUtils.getInstance().getInt(USER_SEX, 0);
    }

    public static HkUserManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 参考python,存储变化前请先更新user
     */
    public void save() {
        SPUtils.getInstance().put(USER_UID, user.uid);
        SPUtils.getInstance().put(USER_NICKNAME, user.nickname);
        SPUtils.getInstance().put(USER_AVATAR, user.avatar);
        SPUtils.getInstance().put(USER_EMAIL, user.email);
        SPUtils.getInstance().put(USER_PHONE, user.phone);
        SPUtils.getInstance().put(USER_SIGNATURE, user.signature);
        SPUtils.getInstance().put(USER_DATE_JOINED, user.date_joined);
        SPUtils.getInstance().put(USER_LAST_LOGIN, user.last_login);
        SPUtils.getInstance().put(USER_SEX, user.sex);
    }

    /**
     * 移除所有Sp数据，需要持久化保存不被删除的请使用Cache
     */
    public void clear() {
        user = null;
        SPUtils.getInstance().remove(USER_UID);
        SPUtils.getInstance().remove(USER_NICKNAME);
        SPUtils.getInstance().remove(USER_AVATAR);
        SPUtils.getInstance().remove(USER_EMAIL);
        SPUtils.getInstance().remove(USER_PHONE);
        SPUtils.getInstance().remove(USER_SIGNATURE);
        SPUtils.getInstance().remove(USER_DATE_JOINED);
        SPUtils.getInstance().remove(USER_LAST_LOGIN);
        SPUtils.getInstance().remove(USER_SEX);
    }

    public boolean isLogin() {
        return user != null;
    }

    public void checkLogin(Context context) {
        if (!isLogin()) {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    public boolean isAdmin() {
        return user != null && user.uid == 1;
    }
}
