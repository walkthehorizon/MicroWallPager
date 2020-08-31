package com.shentu.paper.mvp.ui.my

enum class ContentMode(name: String , id:Int) {
    ANIM("动漫",0),//动漫
    COS("Cos",1),//Cos
    PHOTO("写真",2);//写真

    companion object{
        fun getContentMode(name: String): ContentMode {
            return when (name) {
                COS.name -> COS
                PHOTO.name -> PHOTO
                else -> ANIM
            }
        }

        fun getModeStr(modes:List<String>):String{
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