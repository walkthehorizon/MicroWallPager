package com.shentu.wallpaper.app

import android.provider.Settings
import com.blankj.utilcode.util.SPUtils
import com.shentu.wallpaper.model.entity.MicroUser

class HkUserManager private constructor() {
    private val UID = "user_uid"
    private val USER_AVATAR = "user_avatar"
    private val USER_NICKNAME = "user_nickname"
    private val USER_PHONE = "user_phone"
    private val USER_EMAIL = "user_email"
    private val USER_SIGNATURE = "user_signature"
    private val USER_DATE_JOINED = "user_date_joined"
    private val USER_LAST_LOGIN = "user_last_login"
    private val USER_SEX = "user_sex"
    private val USER_PEA = "user_pea"
    private val USER_TOKEN = "user_token"
    private val USER_VIP = "user_vip"
    private val USER_SVIP = "user_svip"
    var user: MicroUser = MicroUser()

    /**
     * 参考python,存储变化前请先更新user
     */
    fun save() {
        SPUtils.getInstance().put(UID, user.uid)
        SPUtils.getInstance().put(USER_NICKNAME, user.nickname)
        SPUtils.getInstance().put(USER_AVATAR, user.avatar)
        SPUtils.getInstance().put(USER_EMAIL, user.email)
        SPUtils.getInstance().put(USER_PHONE, user.phone)
        SPUtils.getInstance().put(USER_SIGNATURE, user.signature)
        SPUtils.getInstance().put(USER_DATE_JOINED, user.date_joined)
        SPUtils.getInstance().put(USER_LAST_LOGIN, user.last_login)
        SPUtils.getInstance().put(USER_SEX, user.sex)
        SPUtils.getInstance().put(USER_PEA, user.pea)
        SPUtils.getInstance().put(USER_TOKEN, user.token)
        SPUtils.getInstance().put(USER_VIP, user.vip)
        SPUtils.getInstance().put(USER_SVIP, user.svip)
    }

    /**
     * 移除所有Sp数据，需要持久化保存不被删除的请使用Cache
     */
    fun clear() {
        user = MicroUser()//重置
        SPUtils.getInstance().remove(UID)
        SPUtils.getInstance().remove(USER_NICKNAME)
        SPUtils.getInstance().remove(USER_AVATAR)
        SPUtils.getInstance().remove(USER_EMAIL)
        SPUtils.getInstance().remove(USER_PHONE)
        SPUtils.getInstance().remove(USER_SIGNATURE)
        SPUtils.getInstance().remove(USER_DATE_JOINED)
        SPUtils.getInstance().remove(USER_LAST_LOGIN)
        SPUtils.getInstance().remove(USER_SEX)
        SPUtils.getInstance().remove(USER_PEA)
        SPUtils.getInstance().remove(USER_TOKEN)
        SPUtils.getInstance().remove(USER_VIP)
        SPUtils.getInstance().remove(USER_SVIP)
    }

    fun updateKandou(change: Int) {
        user.pea += change
        SPUtils.getInstance().put(USER_PEA, user.pea)
    }

    val uuid: String
        get() = Settings.System.getString(AppLifecycleImpl.instance.contentResolver, Settings.Secure.ANDROID_ID)

    val isLogin: Boolean
        get() = SPUtils.getInstance().getInt(UID, -1) != -1

    val isAdmin: Boolean
        get() = isLogin && user.uid == 1

    val uid: Int
        get() = if (isLogin) user.uid else -1

    val token: String
        get() = if (isLogin) user.token else ""

    companion object {
        val instance = HkUserManager()
    }

    init {
        if (isLogin) {
            user.uid = SPUtils.getInstance().getInt(UID, -1)
            user.nickname = SPUtils.getInstance().getString(USER_NICKNAME, "")
            user.avatar = SPUtils.getInstance().getString(USER_AVATAR, "")
            user.email = SPUtils.getInstance().getString(USER_EMAIL, "")
            user.phone = SPUtils.getInstance().getString(USER_PHONE, "")
            user.signature = SPUtils.getInstance().getString(USER_SIGNATURE, "")
            user.date_joined = SPUtils.getInstance().getString(USER_DATE_JOINED, "")
            user.last_login = SPUtils.getInstance().getString(USER_LAST_LOGIN, "")
            user.sex = SPUtils.getInstance().getInt(USER_SEX, 0)
            user.pea = SPUtils.getInstance().getInt(USER_PEA, 0)
            user.token = SPUtils.getInstance().getString(USER_TOKEN, "")
            user.vip = SPUtils.getInstance().getBoolean(USER_VIP, false)
            user.svip = SPUtils.getInstance().getBoolean(USER_SVIP, false)
        }
    }
}