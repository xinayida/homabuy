package com.nhzw.service;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.nhzw.action.ActionType;
import com.nhzw.common.service.R;
import com.nhzw.component.AppComponent;
import com.xinayida.lib.rxflux.Action;
import com.xinayida.lib.rxflux.Dispatcher;
import com.xinayida.lib.utils.NetUtil;
import com.xinayida.lib.utils.SpUtil;

import java.util.Stack;

import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 应用主Service，与其他service不同，没有注册过程，由主Application直接构造生成
 * Created by ww on 2017/9/12.
 */

public enum AppService {
    instance;

    public static boolean DEBUG;
    public static String DOMAIN_API;
    public static String DOMAIN_LOGIN;
    public static String DOMAIN_UPDATE;
    public static String DOWNLOAD_LINK = "http://www.homabuy.com/install/homabuy.apk";
    public static String SHARE_LINK = "http://www.homabuy.com/share.html";
    private AppComponent appComponent;
    private String token;
    private int uid = -1;
    private Activity curActivity;
    private Stack<Activity> activityStack;//activity栈

    public void setAppComponent(AppComponent appComponent) {
        this.appComponent = appComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void setCurActivity(Activity activity) {
        curActivity = activity;
    }

    public Activity getCurActivity() {
        return curActivity;
    }

    public void pushOneActivity(Activity actvity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(actvity);
    }

    public void popOneActivity(Activity activity) {
        if (activityStack != null && activityStack.size() > 0) {
            if (activity != null) {
                activityStack.remove(activity);
            }
        }
    }

    public void finishAllActivity() {
        if (activityStack != null) {
            while (activityStack.size() > 0) {
                Activity activity = activityStack.lastElement();
                if (activity == null) break;
                popOneActivity(activity);
                activity.finish();
            }
        }
    }

    public void showToast(String message, boolean isLong) {
        if (getCurActivity() == null) {
            return;
        }
        getCurActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getCurActivity(), message, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showToast(String msg) {
        showToast(msg, false);
    }

    public void showNetError() {
        showNetError(curActivity.getString(R.string.network_error));
    }

    public void showNetError(String str) {
        showToast(str, false);
    }

    public boolean noNetworkToast(String... str) {
        if (NetUtil.isConnected(getAppContext())) {
            return false;
        }
        if (str == null || str.length == 0) {
            showNetError();
        } else {
            showNetError(str[0]);
        }
        return true;
    }

    /**
     * 让在前台的 activity,使用 snackbar 显示文本内容
     *
     * @param message
     * @param isLong
     */
    public void showSnackbar(String message, boolean isLong) {
        if (getCurActivity() == null) {
            return;
        }
        View view = getCurActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar.make(view, message, isLong ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT).show();
    }

    public void showSnackbar(String msg) {
        showSnackbar(msg, false);
    }

    public Context getAppContext() {
        return appComponent.getApp();
    }


    /**
     * 异步Http请求
     *
     * @param url
     * @param callback
     */
    public void asyncHttp(String url, Callback callback) {
        asyncHttp(url, callback, null);
    }

    public void asyncHttp(String url, Callback callback, RequestBody body) {
        Request.Builder requestBuilder = getBuilder(url, body);
        Request request = requestBuilder.build();
        getAppComponent().getOkHttpClient().newCall(request).enqueue(callback);
    }

    private Request.Builder getBuilder(String url, RequestBody body) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        //可以省略，默认是GET请求
        //requestBuilder.method("GET", null);
        if (body != null) {
            requestBuilder.method("POST", body);
        }
        return requestBuilder;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void logout(String toast) {
        if(toast != null) {
            showToast(toast, false);
        }
        setUid(-1);
        setToken(null);
        SpUtil.getInstance().removeBean(AccountInfo.class);
        SpUtil.getInstance().writeString("token", null);
        Dispatcher.get().postAction(Action.type(ActionType.LOGOUT).build());

    }

    public String getToken() {
        return token;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public boolean isLogined() {
        return token != null;
    }

}
