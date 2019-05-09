package com.horizon.netbus;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

    /**
     * 连接上网络
     */
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.e(NetworkCallbackImpl.class.getSimpleName(), "网络已连接");
    }

    /**
     * 网络连接发生变化,会重复调用
     */
    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.e(NetworkCallbackImpl.class.getSimpleName(), "网络变更为：WIFI");
            NetBus.getInstance().post(NetType.WIFI);
        } else {
            Log.e(NetworkCallbackImpl.class.getSimpleName(), "网络变更为：GPRS");
            NetBus.getInstance().post(NetType.GPRS);
        }
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);
        Log.e(NetworkCallbackImpl.class.getSimpleName(), linkProperties.toString());
    }

    /**
     * 连接断开，和onAvailable成对出现
     */
    @Override
    public void onLost(Network network) {
        super.onLost(network);
        Log.e(NetworkCallbackImpl.class.getSimpleName(), "网络已断开");
        NetBus.getInstance().post(NetType.NONE);
    }
}
