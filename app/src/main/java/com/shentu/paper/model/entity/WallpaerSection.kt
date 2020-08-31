package com.shentu.paper.model.entity

import com.chad.library.adapter.base.entity.SectionEntity

class WallpaerSection : SectionEntity<Wallpaper?> {
    constructor(isHeader: Boolean, header: String?) : super(isHeader, header) {}
    constructor(wallpaper: Wallpaper?) : super(wallpaper) {}
}