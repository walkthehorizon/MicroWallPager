package com.shentu.netbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shentu.netbus.utils.Constant;
import com.shentu.netbus.utils.NetworkUtils;

class NetworkStateReceiver extends BroadcastReceiver {
    private NetType netType;


    public NetworkStateReceiver() {
        netType = NetType.NONE;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.e(NetworkStateReceiver.class.getSimpleName(), "intent or intent.getAction() is null");
            return;

        }
        if (intent.getAction().equalsIgnoreCase(Constant.INTENT_ACTION)) {
            netType = NetworkUtils.getNetType();
            switch (NetworkUtils.getNetType()) {
                case WIFI:
                    Log.e(NetworkStateReceiver.class.getSimpleName(), "WIFI==广播");
                case GPRS:
                    Log.e(NetworkStateReceiver.class.getSimpleName(), "GPRS==广播");
                case NONE:
                    Log.e(NetworkStateReceiver.class.getSimpleName(), "None==广播");
                default:
                    Log.e(NetworkStateReceiver.class.getSimpleName(), "Auto==广播");
            }
            NetBus.getInstance().post(netType);
        }
    }
}
