package com.nhzw.component.lib.route;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by ww on 2017/9/12.
 */

public class Router implements IRouter {
    private HashMap<String, Class> routeMap = new HashMap<>();
    private static Router router = new Router();

    private Router() {
    }

    public static Router getInstance() {
        return router;
    }

    @Override
    public void registerRouter(String route, Class activityClass) {
        routeMap.put(route, activityClass);

    }

    @Override
    public void unregisterRouter(String route) {
        routeMap.remove(route);
    }

    @Override
    public boolean open(Context context, String route) {
        return open(context, route, null);
    }

    @Override
    public boolean open(Context context, String route, Bundle bundle) {
        if (routeMap.containsKey(route)) {
            Class clazz = routeMap.get(route);
            open(context, clazz, bundle);
            return true;
        }
        return false;
    }

    public void open(Context context, Class clazz) {
        open(context, clazz, null);
    }

    public void open(Context context, Class clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//防止重复调用
        intent.putExtras(bundle == null ? new Bundle() : bundle);
        context.startActivity(intent);
    }

    public void open(Activity context, Class clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//防止重复调用
        intent.putExtras(bundle == null ? new Bundle() : bundle);
        context.startActivityForResult(intent, requestCode);
    }
}
