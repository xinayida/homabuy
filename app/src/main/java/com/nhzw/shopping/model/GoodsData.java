package com.nhzw.shopping.model;

import com.google.gson.JsonObject;
import com.nhzw.shopping.model.entity.GoodsInfo;
import com.xinayida.lib.annotation.NotProguard;


import java.util.List;

/**
 * Created by ww on 2017/10/23.
 */
@NotProguard
public class GoodsData {
    public String lastId;
    public Long ts;
    public List<GoodsInfo> list;
    public JsonObject cursor;
//    public int size; //没用
}
