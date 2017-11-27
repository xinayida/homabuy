package com.nhzw.rx;

import com.xinayida.lib.annotation.NotProguard;

/**
 * 请求返回基类
 * Created by ww on 2017/10/23.
 */
@NotProguard
public class Response<T> {

    public String result;
    public String code;
    public String msg;
    public T data;

    @Override
    public String toString() {
        return "BaseResult{" +
                "result='" + result + '\'' +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public boolean success() {
        return "ok".equalsIgnoreCase(code);
    }
}
