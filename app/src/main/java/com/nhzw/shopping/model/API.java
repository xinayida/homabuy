package com.nhzw.shopping.model;

import com.nhzw.rx.ResponseVoid;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ww on 2017/10/13.
 */

public interface API {
    String OK = "ok";

    /**
     * 首页商品列表
     */
//    @GET("goods/list")
//    Observable<GoodsListResult> getGoods(@Query("size") int size, @Query("lastId") String lastId);

    @POST("goods/list")
    Observable<GoodsListResult> getGoods(@Body RequestBody requestBody);

    /**
     * 心愿单列表
     *
     * @param size     每页个数
     * @param lastTime 上次刷新时间（接口返回）
     */
    @GET("goods/likeList")
    Observable<GoodsListResult> getLikeList(@Query("size") int size, @Query("ts") Long lastTime);

    /**
     * 商品详情
     *
     * @param id 商品id
     */
    @GET("goods/details")
    Observable<GoodsDetailResult> getGoodsDetail(@Query("id") String id);

    /**
     * 喜欢或不喜欢
     *
     * @param goodsId  商品id
     * @param likeType "LIKE" : "DISLIKE";
     */
    @GET("goods/like")
    Observable<ResponseVoid> like(@Query("id") String goodsId, @Query("likeType") String likeType);

    /**
     * 从心愿单中移除喜欢商品
     *
     * @param goodsId 商品id
     */
    @GET("goods/removeLike")
    Observable<ResponseVoid> removeLike(@Query("id") String goodsId);

    /**
     * 是否有礼物
     */
    @GET("goods/hasNewestWin")
    Observable<BooleanResult> hasPrize();

    /**
     * 中奖点击通知
     */
    @GET("goods/notifyWin")
    Observable<ResponseVoid> notifyWin(@Query("id") String goodsId);

    /**
     * 是否有联系方式
     */
    @GET("contact/hasContact")
    Observable<BooleanResult> hasContactInfo();

    /**
     * 获取中奖列表
     */
    @GET("goods/newestWinList")
    Observable<PirzeListResult> getPrizeList();

    /**
     * 保存地址。 —— 由姚宇编辑于2017/11/1.<br/><br/>
     */
    @FormUrlEncoded
    @POST("contact/saveContact")
    Observable<ResponseVoid> saveContact(@Field("name") String name, @Field("phone") String phone,
                                         @Field("address") String address);

    @GET("contact/list")
    Observable<ContactListResult> contactList();

    @GET("versionUpdate")
    Observable<UpdateResult> checkUpdate();

}
