package com.shentu.wallpager.di;

import com.blankj.utilcode.util.SPUtils;
import com.shentu.wallpager.mvp.model.entity.User;

public class HkUserManager {
    protected String USER_UID = "USER_UID";
    protected String USER_AVATAR = "USER_AVATAR";
    protected String USER_NICNAME = "USER_NICNAME";

    public User user;

    private static final class SingletonHolder{
        private static final HkUserManager INSTANCE = new HkUserManager();
    }

    HkUserManager() {
        user = new User();
        user.uid = getUid();
        user.nickname = getNickname();
        user.avatar = getAvatar();
    }

    public static HkUserManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void putUid(String uid) {
        user.uid = uid;
        SPUtils.getInstance().put(USER_UID, uid);
    }

    public String getUid() {
        return SPUtils.getInstance().getString(USER_UID,"");
    }

    public String getAvatar() {
        return SPUtils.getInstance().getString(USER_AVATAR,"");
    }

    public void setAvatar(String avatar) {
        user.avatar = avatar;
        SPUtils.getInstance().put(USER_AVATAR, avatar);
    }

    public String getNickname() {
        return SPUtils.getInstance().getString(USER_NICNAME,"");
    }

    public void setNickname(String nickname) {
        user.nickname = nickname;
        SPUtils.getInstance().put(USER_NICNAME, nickname);
    }
}
