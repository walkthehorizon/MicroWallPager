package com.shentu.wallpaper.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class Subject implements MultiItemEntity {
    public static final int ITEM_VIEW_1 = 1;//魅族壁纸
    public static final int ITEM_VIEW_2 = 2;//cosplay
    public static final int ITEM_VIEW_3 = 3;//性感美女

    public int id;
    public String name;
    public String cover;
    public String description;
    public String tag;
    public String created;
    public int bgColor;
    public String cover_1;
    public String cover_2;
    public int type;

    public Subject() {
        type = ITEM_VIEW_2;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
