package com.nhzw.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ww on 2017/10/18.
 */

public class DoubleClickController {
    public static final String TAG = "DC";
    public static final Map<String, Long> map = new HashMap<>();

    public static boolean isDoubleClick() {
        return isDoubleClick(TAG, 400);
    }

    public static boolean isDoubleCLick(String tag) {
        return isDoubleClick(tag, 400);
    }

    public static boolean isDoubleCLick(long duration) {
        return isDoubleClick(TAG, duration);
    }

    public static boolean isDoubleClick(String tag, long duration) {
        long lastClickTime = map.get(tag) == null ? 0l : map.get(tag);
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        map.put(tag, time);
        if (0 <= timeD && timeD < duration) {
            return true;
        }
        return false;
    }
}
