package com.nhzw.shopping.model.entity;

import com.xinayida.lib.annotation.NotProguard;

/**
 * Created by ww on 2017/10/16.
 */
@NotProguard
public class GoodsInfo {
    public static final String TYPE_WIN = "WIN";
    public static final String TYPE_NORMAL = "NORMAL";
    public static final String TYPE_NOTIFIED = "NOTIFIED";

    public String id;
    public String goodRate;
    public String goodRateStr;
    public String image;
    public String name;
    public String originalPrice;
    public String platformId;
    public String price;
    public String soucrcesType;
    public String winType;//NORMAL,WIN
    public String shareUrl;
    public String shareWinUrl;
}
