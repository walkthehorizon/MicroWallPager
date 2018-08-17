package com.shentu.wallpager.mvp.model.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class Subject implements MultiItemEntity {
    public static final int ITEM_VIEW_1 = 1;
    public static final int ITEM_VIEW_2 = 2;

    public int id;
    public String name;
    public String cover;
    public String description;
    public User owner;

    @Override
    public int getItemType() {
        return ITEM_VIEW_2;
    }
}
