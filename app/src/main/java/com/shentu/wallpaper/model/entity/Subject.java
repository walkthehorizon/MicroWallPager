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

    @Override
    public int getItemType() {
        return ITEM_VIEW_2;
    }
}
