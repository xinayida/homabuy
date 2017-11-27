package com.nhzw.rx;


import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.nhzw.component.lib.route.Router;
import com.nhzw.service.AppService;

import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 错误预处理
 * Created by ww on 2017/9/18.
 */

public abstract class RxObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable t) {
        String msg = "请求失败 ";
        if (t instanceof AuthException) {
            AppService.instance.logout("Token失效，请重新登录");
            Router.getInstance().open(AppService.instance.getCurActivity(), "login");
            return;
        } else if (t instanceof UnknownHostException) {
            msg = "网络不可用 ";
        } else if (t instanceof SocketTimeoutException) {
            msg = "网络请求超时 ";
        } else if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            msg = convertStatusCode(httpException);
        } else if (t instanceof JsonParseException || t instanceof ParseException || t instanceof JSONException || t instanceof JsonIOException) {
            msg = "数据解析错误 ";
        }
        msg += t.getMessage();
//        AppService.instance.showSnackbar(msg, true);
        if (AppService.instance.DEBUG) {
            Log.e("OkHttp", msg);
        }
    }

    @Override
    public void onComplete() {

    }

    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向到其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }
}
