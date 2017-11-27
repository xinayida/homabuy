package com.nhzw.shopping.model.repository;

import com.nhzw.base.BaseRepository;
import com.nhzw.base.IRepositoryFactory;
import com.nhzw.rx.ResponseVoid;
import com.nhzw.shopping.model.API;
import com.nhzw.shopping.model.GoodsDetailResult;

import io.reactivex.Observable;


/**
 * Created by ww on 2017/10/23.
 */

public class DetailRepository extends BaseRepository {
    public DetailRepository(IRepositoryFactory repositoryFactory) {
        super(repositoryFactory);
    }

    public Observable<GoodsDetailResult> getDetailInfo(String gid) {
        return factory.createRetrofitService(API.class).getGoodsDetail(gid);
    }

    public Observable<ResponseVoid> like(String goodId, String like) {
        return factory.createRetrofitService(API.class).like(goodId, like);
    }

    @Override
    public void onDestory() {

    }
}
