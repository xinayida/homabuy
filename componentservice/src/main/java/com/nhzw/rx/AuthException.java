package com.nhzw.rx;

/**
 * Created by ww on 2017/11/7.
 */

/**
 * 验证错误
 */
public class AuthException extends Exception {

    public String code;

    public AuthException(String code, String message) {
        super(message);
        this.code = code;
    }
}
