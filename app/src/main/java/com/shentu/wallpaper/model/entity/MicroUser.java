/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shentu.wallpaper.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * ================================================
 * User 实体类
 * <p>
 * Created by JessYan on 04/09/2016 17:14
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
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

    @Override
    public String toString() {
        return "uid:" + uid + "nickname:" + nickname + "avatar:" + avatar + "phone:" + phone + "email:" + email;
    }
}
