package com.nhzw.service;

/**
 * Created by ww on 2017/10/13.
 */

public class AccountInfo {
    public String headImg;
    public String nickname;
    public String token;
    public int uid;

    @Override
    public String toString() {
        return "AccountInfo{" +
                "headImg='" + headImg + '\'' +
                ", nickname='" + nickname + '\'' +
                ", token='" + token + '\'' +
                ", uid=" + uid +
                '}';
    }
}
