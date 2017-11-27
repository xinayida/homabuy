package com.nhzw.shopping.model.entity;

import com.xinayida.lib.annotation.NotProguard;

/**
 * Created by ww on 2017/10/23.
 */
//  "detailsUrl":"192.168.16.2:83/html/goods/details/59e96cb9f646f2389c242ff0.html",
//  "goodRate":98,
//  "id":"59e96cb9f646f2389c242ff0",
//  "image": "http://img12.360buyimg.com/N1/jfs/t6010/111/3843138696/73795/bf58700d/5959ab7fN154e56b4.jpg",
//  "images": [
//  "http://img12.360buyimg.com/N1/jfs/t5704/329/1459117714/81627/ba4ba828/59267f92N78dde35d.jpg",
//  "http://img12.360buyimg.com/N1/jfs/t6010/111/3843138696/73795/bf58700d/5959ab7fN154e56b4.jpg",
//  "http://img12.360buyimg.com/N1/jfs/t6115/117/184172293/230947/33fe413c/59267f87N8682e899.jpg",
//  "http://img12.360buyimg.com/N1/jfs/t6064/70/179072405/94796/231566a0/59267f88N8ec0a337.jpg",
//  "http://img12.360buyimg.com/N1/jfs/t5806/162/1446662913/220238/3951c99/59267f8fNca0b0e92.jpg",
//  "http://img12.360buyimg.com/N1/jfs/t6124/107/174225769/281765/95d0867/59267ffeN42a361cf.jpg"
//  ],
//  "name": "小米Max2 全网通 4GB+64GB 金色 移动联通电信4G手机 双卡双待",
//  "originalPrice": 1699,
//  "platformId": "5159242",
//  "price": 1699,
//  "shopLogo": "http://img30.360buyimg.com/popshop/jfs/t1312/315/315615466/4441/5a865e93/5565f5a2N0b8169ae.jpg",
//  "shopName": "小米京东自营旗舰店",
//  "sourcesType": "JD"
@NotProguard
public class GoodsDetail {
    public String detailsUrl;
    public String id;
    public String goodRate;
    public String goodRateStr;
    public String image;
    public String[] images;
    public String name;
    public String price;
    public String originalPrice;
    public String platformId;
    public String shopLogo;
    public String shopName;
    public String sourcesType;
    public String sourcesTypeStr;
    public String shareUrl;
    public String promotionUrl;
}
