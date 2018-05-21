package com.shentu.wallpager.mvp.model.entity;

public class WallPager {
    public String describe;
    public String url;
    public String name;

    public WallPager(String name, String describe, String url) {
        this.describe = describe;
        this.url = url;
        this.name = name;
    }
}
