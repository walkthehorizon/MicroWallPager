package com.shentu.wallpaper.model.entity;

import com.chad.library.adapter.base.entity.SectionEntity;

public class WallpaerSection extends SectionEntity<Wallpaper> {
    public WallpaerSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public WallpaerSection(Wallpaper wallpaper) {
        super(wallpaper);
    }
}
