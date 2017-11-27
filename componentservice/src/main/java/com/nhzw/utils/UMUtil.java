package com.nhzw.utils;

import com.nhzw.service.AppService;
import com.umeng.analytics.MobclickAgent;
import com.xinayida.lib.utils.L;

import java.util.HashMap;
import java.util.Map;

/**
 * =================================
 * 描述: 友盟统计工具.<br/><br/>
 * 作者：由姚宇编辑于2017/11/7.<br/><br/>
 * =================================
 */
public class UMUtil {

    public static void submitCustomEvent(final String eventId) {
        MobclickAgent.onEvent(AppService.instance.getAppContext(), eventId);

    }

    /**
     * 友盟计数统计
     * @param eventId 事件ID
     * @param key     参数
     * @param value   参数值
     */
    public static void submitCustomEvent(final String eventId, final String key, final String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put(key, value);
        submitCustomEvent(eventId, map);
    }


    /**
     * 友盟计数统计
     * @param eventId 事件ID
     * @param map     参数
     */
    public static void submitCustomEvent(final String eventId, final Map<String, String> map) {
        if (map != null) {
            L.d("umeng", "event: " + eventId + " params: " + map.toString());
        } else {
            L.d("umeng", "event: " + eventId + " params is null ");
        }
        MobclickAgent.onEvent(AppService.instance.getAppContext(), eventId, map);
    }
}
