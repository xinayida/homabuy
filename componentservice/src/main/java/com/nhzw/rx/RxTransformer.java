package com.nhzw.rx;

import org.json.JSONObject;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 错误预处理+切换线程
 * Created by ww on 2017/10/27.
 */

public class RxTransformer<T> {

    /**
     * 切换线程、处理异常以及提取data
     */
    public static <T> ObservableTransformer<Response<T>, T> trans() {
        return responseObservable -> responseObservable.map(new Function<Response<T>, T>() {
            @Override
            public T apply(@NonNull Response<T> tResponse) throws Exception {
                if (!tResponse.success()) {
                    //Log.d("Stefan", "response failed: " + tResponse);
                    if ("auth.error".equalsIgnoreCase(tResponse.code)) {
                        throw new AuthException(tResponse.code, tResponse.msg);
                    }
                    throw new Exception(tResponse.msg);
                }
                if (tResponse.data == null) {
                    tResponse.data = (T) new JSONObject();
                }
                return tResponse.data;
            }
        })
//                .onErrorResumeNext(throwable -> {
//            return Observable.error(throwable);
//        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 切换线程和处理异常
     */
    public static <T> ObservableTransformer<T, T> switchSchedulers() {
        return observable -> observable.map(new Function<T, T>() {
            @Override
            public T apply(@NonNull T t) throws Exception {
                if (t instanceof Response && !((Response) t).success()) {
                    throw new Exception(((Response) t).msg);
                }
                return t;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
