package com.nhzw.shopping.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.nhzw.base.BaseViewModel;
import com.nhzw.rx.RxObserver;
import com.nhzw.rx.RxTransformer;
import com.nhzw.shopping.model.repository.DetailRepository;
import com.nhzw.shopping.model.entity.GoodsDetail;

import org.json.JSONObject;

/**
 * Created by ww on 2017/10/23.
 */

public class DetailViewModel extends BaseViewModel<DetailRepository> {
    private MutableLiveData<GoodsDetail> goodsDetail = new MutableLiveData<>();

    @Override
    public void onStart() {

    }

    public void getGoodsDetail(String gid) {
        setLoadingStatus(LOADINGMORE);
        repository.getDetailInfo(gid).doOnSubscribe(this::addDispose).compose(RxTransformer.trans())
                .subscribe(new RxObserver<GoodsDetail>() {
                    @Override
                    public void onNext(GoodsDetail detail) {
                        goodsDetail.setValue(detail);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        goodsDetail.setValue(new GoodsDetail());
                    }
                });
    }

    public void like(String goodId, boolean isLike) {
        String like = isLike ? "LIKE" : "DISLIKE";
        repository.like(goodId, like).doOnSubscribe(this::addDispose).compose(RxTransformer.trans())
                .subscribe(new RxObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {

                    }
                });
    }


    public MutableLiveData<GoodsDetail> getGoodsDetail() {
        return goodsDetail;
    }
}
