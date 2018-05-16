package com.shentu.wallpager.app.utils;

import android.os.Build;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HkUtils {
    private final String DEVICE_ID = "DEVICE_ID";
    public String deviceId;

    private static class SingletonHolder {
        private static final HkUtils INSTANCE = new HkUtils();
    }

    public HkUtils() {
        deviceId = getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = generateDeviceId();
        }
    }

    public static HkUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public String getDeviceId() {
        return SPUtils.getInstance().getString(DEVICE_ID, "");
    }

    public String generateDeviceId() {
        String result;
        try {
            result = toMD5(Build.FINGERPRINT);
            SPUtils.getInstance().put(DEVICE_ID, result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }

    public String getSha1(String val) {
        byte[] data = new byte[0];
        String result = "";
        try {
            data = val.getBytes("utf-8");
            MessageDigest mDigest = MessageDigest.getInstance("sha1");
            byte[] digest = mDigest.digest(data);
            result = byteArrayToHexStr(digest);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
