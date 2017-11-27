package com.nhzw.component.lib.service;

import android.util.Log;

import com.nhzw.component.lib.applicationlike.IApplicationLike;

import java.util.HashMap;

/**
 * Created by mrzhang on 2017/6/15.
 */

public class ServiceManager {

    private HashMap<String, Object> services = new HashMap<>();

    private static volatile ServiceManager instance;

    private ServiceManager() {
    }

    public static ServiceManager getInstance() {
        if (instance == null) {
            synchronized (ServiceManager.class) {
                if (instance == null) {
                    instance = new ServiceManager();
                }
            }
        }
        return instance;
    }

    public synchronized void addService(Class serviceName, Object serviceImpl) {
        if (serviceName == null || serviceImpl == null) {
            return;
        }
//        Log.d("Stefan", "addService: " + serviceName + "  " + serviceImpl);
        services.put(serviceName.getName(), serviceImpl);
    }

    public synchronized <T> T getService(Class<T> service) {
        if (service == null) {
            return null;
        }
        T instance = null;
        String serviceName = service.getName();
        instance = (T) services.get(serviceName);

//        Log.d("Stefan", "getService: " + serviceName + "  " + instance);
        return instance;
//        return services.get(serviceName);
    }

    public synchronized void removeService(Class serviceName) {
        if (serviceName == null) {
            return;
        }
        services.remove(serviceName.getName());
    }

    public void registerComponent(String classname) {
        try {
            Class clazz = Class.forName(classname);
            IApplicationLike applicationLike = (IApplicationLike) clazz.newInstance();
            applicationLike.onCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterComponent(String classname) {
        try {
            Class clazz = Class.forName(classname);
            IApplicationLike applicationLike = (IApplicationLike) clazz.newInstance();
            applicationLike.onStop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
