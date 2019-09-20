package com.shentu.wallpaper.model.entity;

import com.google.gson.annotations.SerializedName;

public class MicroUser {
    @SerializedName("id")
    public Integer uid;
    public String nickname;
    public String avatar;
    public String phone;
    public String email;
    public String signature;
    public String date_joined;
    public String last_login;
    public Integer sex;//默认保密
    public Integer pea = 0;
    public String token = "";

    @Override
    public String toString() {
        return "uid:" + uid + "nickname:" + nickname + "avatar:" + avatar + "phone:" + phone + "email:" + email;
    }
}
