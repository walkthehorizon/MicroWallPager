package com.shentu.wallpaper.app

import com.shentu.wallpaper.model.entity.AppUpdate
import java.util.*

object Constant {
    var LAST_LOGIN_ACCOUNT = "last_login_account"
    var MAIN_TAB_TITLES = Arrays.asList("推荐", "我的")
    var BANNER_COUNT = 5
    var URGE_AD_DIALOG = "urge_ad_dialog"
    var LAST_SIGN_TIME = "last_sign_time"
    /**
     * 下载询问
     */
    var DOWNLOAD_TYPE = "download_type"
    var appUpdate: AppUpdate? = null
    var LAST_NOTIFY_TIME = "LAST_NOTIFY_TIME"
    var MICRO_BASE_URL = "https://wmmt119.top"
    const val BASE_WALLPAPER_SHARE_URL = "https://wmmt119.top/wallpaper/"
    const val GITHUB_URL = "https://github.com/walkthehorizon/MicroWallPager/blob/master/README.md"
    const val WEB_PRIVACY = "https://wmmt119.top/privacy"
    const val WEB_SERVER = "https://wmmt119.top/agreement"
}