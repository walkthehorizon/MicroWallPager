package com.horizon.netbus;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;
import static android.net.NetworkCapabilities.TRANSPORT_WIFI;

public class NetBus {
    private Map<Object, List<MethodManager>> networkList;
    private static volatile NetBus manager;
    private Application application;
    private NetworkStateReceiver receiver;
    private NetworkCallbackImpl networkCallback;
    private ConnectivityManager connectivityManager;

    private NetBus() {

    }

    public static NetBus getInstance() {
        if (manager == null) {
            synchronized (NetBus.class) {
                if (manager == null) {
                    manager = new NetBus();
                }
            }
        }
        return manager;
    }

    public Application getApplication() {
        if (application == null) {
            throw new RuntimeException("please call init method in your app");
        }
        return application;
    }


    public void init(Application app) {
        this.application = app;
        networkList = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new NetworkCallbackImpl();
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addCapability(NET_CAPABILITY_INTERNET)
                    .addTransportType(TRANSPORT_CELLULAR)
                    .addTransportType(TRANSPORT_WIFI);
            NetworkRequest request = builder.build();
            connectivityManager = (ConnectivityManager) NetBus.getInstance()
                    .getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.registerNetworkCallback(request, networkCallback);
            }
        } else {
            receiver = new NetworkStateReceiver();
            //广播注册
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            application.registerReceiver(receiver, filter);
        }
    }

    public void post(NetType netType) {
        Set<Object> set = networkList.keySet();
        for (final Object object : set) {
            List<MethodManager> methodManagers = networkList.get(object);
            if (methodManagers != null) {
                for (final MethodManager method : methodManagers) {
                    if (method.getType().isAssignableFrom(netType.getClass())) {
                        switch (method.getNetType()) {
                            case AUTO:
                                invoke(method, object, netType);
                                break;
                            case WIFI:
                                if (netType == NetType.WIFI || netType == NetType.NONE) {
                                    invoke(method, object, netType);
                                }
                                break;
                            case GPRS:
                                if (netType == NetType.GPRS || netType == NetType.NONE) {
                                    invoke(method, object, netType);
                                }
                                break;
                            case NONE:
                                break;
                            default:
                                break;
                        }
                    }

                }
            }
        }
    }

    private void invoke(MethodManager method, Object object, NetType netType) {
        Method me = method.getMethod();
        try {
            me.invoke(object, netType);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private List<MethodManager> findAnnotation(Object register) {
        List<MethodManager> methodManagers = new ArrayList<>();
        Class<?> aClass = register.getClass();
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            NetWork annotation = method.getAnnotation(NetWork.class);
            if (annotation == null) {
                continue;
            }
            Type genericReturnType = method.getGenericReturnType();
            if (!genericReturnType.toString().equals("void")) {
                throw new RuntimeException(method.getName() + "Method return must be void");
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new RuntimeException(method.getName() + "Method can only have one parameter");
            }

            MethodManager methodManager = new MethodManager(parameterTypes[0], annotation.netType(), method);
            methodManagers.add(methodManager);
        }
        return methodManagers;
    }

    public void register(Object register) {
        if (networkList == null) {
            throw new RuntimeException("please init in application before use NetBus");
        }
        List<MethodManager> methodManagers = networkList.get(register);
        if (methodManagers == null) {
            methodManagers = findAnnotation(register);
            networkList.put(register, methodManagers);
        }
    }

    public void unRegister(Object register) {
        if (!networkList.isEmpty()) {
            networkList.remove(register);
        }
    }

    public void unRegisterAll() {
        if (!networkList.isEmpty()) {
            networkList.clear();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (connectivityManager != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            }
        } else {
            NetBus.getInstance().getApplication().unregisterReceiver(receiver);
        }
        networkList = null;
    }
}