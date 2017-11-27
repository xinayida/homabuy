package com.nhzw.shopping.model.repository;

import com.nhzw.base.BaseRepository;
import com.nhzw.base.IRepositoryFactory;
import com.nhzw.rx.ResponseVoid;
import com.nhzw.shopping.model.API;
import com.nhzw.shopping.model.BooleanResult;
import com.nhzw.shopping.model.GoodsListResult;
import com.nhzw.shopping.model.PirzeListResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;

/**
 * Created by ww on 2017/9/29.
 */

public class MainRepository extends BaseRepository {
    public static final int PAGE_SIZE = 10;

    public MainRepository(IRepositoryFactory repositoryFactory) {
        super(repositoryFactory);
    }

    public Observable<GoodsListResult> getGoods(RequestBody body) {
//        if (pageSize == null) {
//            pageSize = PAGE_SIZE;
//        }
        return factory.createRetrofitService(API.class).getGoods(body);
    }

    public Observable<GoodsListResult> getLikeList(Long timeStamp) {
        return factory.createRetrofitService(API.class).getLikeList(PAGE_SIZE, timeStamp);
    }

    public Observable<ResponseVoid> like(String goodId, String like) {
        return factory.createRetrofitService(API.class).like(goodId, like);
    }

    public Observable<ResponseVoid> removeLike(String goodsId) {
        return factory.createRetrofitService(API.class).removeLike(goodsId);
    }

    public Observable<ResponseVoid> notifyWin(String goodsId) {
        return factory.createRetrofitService(API.class).notifyWin(goodsId);
    }

    public Observable<BooleanResult> hasPrize() {
        return factory.createRetrofitService(API.class).hasPrize();
    }

    public Observable<PirzeListResult> getPrizeList() {
        return factory.createRetrofitService(API.class).getPrizeList();
    }

    public Observable<BooleanResult> hasContactInfo() {
        return factory.createRetrofitService(API.class).hasContactInfo();
    }

    @Override
    public void onDestory() {

    }
}
