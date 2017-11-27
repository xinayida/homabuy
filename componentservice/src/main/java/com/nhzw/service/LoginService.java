package com.nhzw.service;

import android.graphics.Bitmap;

/**
 * Created by ww on 2017/9/12.
 */

public interface LoginService {
    void loginWeichat();

    /**
     * 分享
     *
     public static final int WXSceneSession = 0;聊天
     public static final int WXSceneTimeline = 1;朋友圈
     public static final int WXSceneFavorite = 2;收藏
     */
    void showShare( String url, String title, String description, Bitmap bitmap);
}
