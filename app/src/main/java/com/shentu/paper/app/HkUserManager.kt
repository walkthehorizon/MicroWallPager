package com.shentu.paper.app

import android.provider.Settings
import com.blankj.utilcode.util.SPUtils
import com.shentu.paper.model.entity.MicroUser

object HkUserManager {
    private val UID = "user_uid"
    private val USER_AVATAR = "user_avatar"
    private val USER_NICKNAME = "user_nickname"
    private val USER_PHONE = "user_phone"
    private val USER_EMAIL = "user_email"
    private val USER_SIGNATURE = "user_signature"
    private val USER_SEX = "user_sex"
    private val USER_PEA = "user_pea"
    private val USER_TOKEN = "user_token"
    private val USER_VIP = "user_vip"
    private val OPRICE = "user_oprice"
    private val NPRICE = "user_nprice"
    private val SHOW_DONATE_INTERVAL = "show_donate_interval"

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
        SPUtils.getInstance().put(USER_SEX, user.sex)
        SPUtils.getInstance().put(USER_PEA, user.pea)
        SPUtils.getInstance().put(USER_TOKEN, user.token)
        SPUtils.getInstance().put(USER_VIP, user.vip)
        SPUtils.getInstance().put(OPRICE, user.oPrice)
        SPUtils.getInstance().put(NPRICE, user.nPrice)
        SPUtils.getInstance().put(SHOW_DONATE_INTERVAL, user.showDonateInterval)
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
        SPUtils.getInstance().remove(USER_SEX)
        SPUtils.getInstance().remove(USER_PEA)
        SPUtils.getInstance().remove(USER_TOKEN)
        SPUtils.getInstance().remove(USER_VIP)
        SPUtils.getInstance().remove(OPRICE)
        SPUtils.getInstance().remove(NPRICE)
        SPUtils.getInstance().remove(SHOW_DONATE_INTERVAL)
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

    init {
        if (isLogin) {
            user.uid = SPUtils.getInstance().getInt(UID, -1)
            user.nickname = SPUtils.getInstance().getString(USER_NICKNAME, "")
            user.avatar = SPUtils.getInstance().getString(USER_AVATAR, "")
            user.email = SPUtils.getInstance().getString(USER_EMAIL, "")
            user.phone = SPUtils.getInstance().getString(USER_PHONE, "")
            user.signature = SPUtils.getInstance().getString(USER_SIGNATURE, "")
            user.sex = SPUtils.getInstance().getInt(USER_SEX, 0)
            user.pea = SPUtils.getInstance().getInt(USER_PEA, 0)
            user.token = SPUtils.getInstance().getString(USER_TOKEN, "")
            user.vip = SPUtils.getInstance().getBoolean(USER_VIP, false)
            user.oPrice = SPUtils.getInstance().getInt(OPRICE, 3)
            user.nPrice = SPUtils.getInstance().getInt(NPRICE, 1)
            user.showDonateInterval = SPUtils.getInstance().getLong(SHOW_DONATE_INTERVAL, 24*60*60*1000)
        }
    }
}