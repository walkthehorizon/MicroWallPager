package com.shentu.paper.mvp.ui.my

enum class ContentMode(val tName: String, val id: Int) {
    COS("Cos", 0),//Cos
    ANIM("动漫", 1),//动漫
    MM("写真", 2);//写真

    companion object {
        fun getContentMode(id: Int): ContentMode {
            return when (id) {
                1 -> ANIM
                2 -> MM
                else -> COS
            }
        }

        fun getModeStr(modes: List<String>): String {
            var modeStr = ""
            for (mode in modes) {
                if (modeStr != "") {
                    modeStr += ","
                }
                modeStr += mode
            }
            return modeStr
        }
    }
}