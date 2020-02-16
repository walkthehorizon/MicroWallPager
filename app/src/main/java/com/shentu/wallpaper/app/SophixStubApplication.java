package com.shentu.wallpaper.app;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.multidex.MultiDex;

import com.shentu.wallpaper.BuildConfig;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 * <p>
 * 实践注意事项：
 * 必须妥善处理打包时生成的mapping文件
 * 热修复更改了类加载器的运行模式，Google的Firebase会认为unsafe导致崩溃，所以不可再接入
 */
public class SophixStubApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(HkApplication.class)
    static class RealApplicationStub {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        initSophix();
    }

    private void initSophix() {
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setSecretMetaData("28343833-1", "e926fbb996e7eb9ae4f96a5f8c3dfd29", "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCpIOEVCXBgXSDN44UjUBV+nZ5ner4ivSMOmfhgRtvIsBqgv+7/JVbWqtxCabX/OXxBMAPQCLwDzZReAYN2l616ja2OrvJ/O3Qb8tmXTXpwifCJl9u645u/REhYZPaseAEPSy8gPyovEAfpL7s7zM27R306r7JNEBuQSf6SSRnIkWobjeXgCZPx11sSKi1wstuSDktiCdWnJvy3HbmldcI/XrpF722dAsJaeJBcSPvMxZeSd+K9DSfnsDRedSwY8Dg3oNwMvv6NCTRPHI77d4h7lLs3pblOPB7I0NK0yMY5ofNypl1rP5VZSFosRUIGNiNb4iQuR+6ChGcKxlwjDJf1AgMBAAECggEBAJXrrVomidUB+IhJvMU2wjcApV/5L/4/Roo09jTx2vWlFZWVTdgiUz3rK2A7sLbTUE/ArkCE3ZVJcNF76fsALSlL2aN3dUJPg3ca6ML0TSrhO7XcRUz/YhpD/xVMOlVb8ySJcvbUxyH8xeZxBznVLDFtB5c0E4u4UZav4NfvsMlWlw41Knhz4j9xbkwSo4MV62OVXtYghb1JaoToz4HTqFPw5Pjgv0O8WXfZBc6d++chjjSSkCgyjCch80CF5oPnVoY41v5fUwQGEft4EISs+VncP/jZanTDyPTOoICZ4OGwc5hgks3GReW01YyzjBsLcKcXN3LZanhdpT+8QpEqrWkCgYEA94K1YisgeQLUWjvgwS/W2aEjjLchEjM4WZGECSB64rFEundSGunq0zxroU1UqWqyQNg9G3e0AQOWwE5Q4Eh9j+GVsLZPRT0mcvbMWQYu4fu4CMIQwIZ6kpOtcc0/+MZAlC1Kxe650wcigbNbNVo4DuNa2Mo+icMtA9GHvv980QsCgYEAru3trBdGNvQ/jUL35JChkIHbcWvV0wSbM95iDJ/dPgSQiL2JHQ9IR01rfAUcYEAVw+66IJ3mtar9IC9M8EM039pgz8VZtgBJvRlXkedwuW+BoUb/XDhRiCadn0DNemMhrnsyj4SFLcgVj9gMjM5Lvn6lP5/tly/Z+drAIf4/2v8CgYAr27iCbbvyVYMFfkjEUtLSe5BOFgFBNVxN2tA3wc6krt7L3+rno/2dcr5rtcVDxAlUHj0tyddWuqkS6I3CQHYnccTRrGx9i2ltw7fQKp1SJAWnX6QSSYIcyRhZ6xHj5lV5LFFso3HAKCqAKyUFXjhCwxzM/4F3beNjItTYDnOyowKBgQCOTdwlPSHPns78WdhTxhzI8/rSwAjIi1y+y1A0OPDAfPIfayDZ1Q7VgnPIBlhZLIkIRXKBCjMe4qTcw93xLpsa6AoNu9wJJR7XiIAmn4KiN8Xar2kZWm9pNRDQ9uSxhWdnC63L9uBop/ZyeC1tdn1D0L8ZjT642SKYEY5vmOfVfQKBgQC2W8iTUBDjs7Y9V3uxDiLBqdaoJn1rA+OVZ/agOIrG4gJPInUFH5hpRaTh4A65Bcr6b53zz+fmjG11xPcBEslCT46ZVQqo5F4nBby5fiS6M1BnPUB5eA8mLYLWD6ONFAljqWgrcAor5VbAPyx1O5apHaoefNJr22lp5rf43S7xlA==")
//                .setEnableDebug(BuildConfig.Debug)
//                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.i(TAG, "sophix load patch success!");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                        }
                        Log.e(TAG, "mode: " + mode + " code: " + code + " info: " + info);
                    }
                }).initialize();
    }
}