package com.nhzw.shopping.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.nhzw.action.ActionType;
import com.nhzw.base.BaseViewModel;
import com.nhzw.component.lib.route.Router;
import com.nhzw.rx.RxObserver;
import com.nhzw.rx.RxTransformer;
import com.nhzw.service.AccountInfo;
import com.nhzw.service.AppService;
import com.nhzw.shopping.model.GoodsData;
import com.nhzw.shopping.model.entity.GoodsInfo;
import com.nhzw.shopping.model.repository.MainRepository;
import com.xinayida.lib.rxflux.Action;
import com.xinayida.lib.rxflux.RxStore;
import com.xinayida.lib.rxflux.StoreObserver;
import com.xinayida.lib.utils.SpUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by ww on 2017/9/29.
 */

public class MainViewModel extends BaseViewModel<MainRepository> implements StoreObserver {
    private RxStore store;
    private MutableLiveData<AccountInfo> accountInfoMutable = new MutableLiveData<>();
    private MutableLiveData<List<GoodsInfo>> goodsList = new MutableLiveData<>();
    private MutableLiveData<List<GoodsInfo>> likeList = new MutableLiveData<>();
    private MutableLiveData<List<GoodsInfo>> prizeList = new MutableLiveData<>();
    private MutableLiveData<Integer> likeLoadingStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> hasContactInfo = new MutableLiveData<>();
    private MutableLiveData<Boolean> hasPrize = new MutableLiveData<>();

//    private String curId;
//    private int itemLeft;//滑动过程中剩余的item数量，退出重进时回复上次浏览的商品

    public boolean pullToRefreshLike;
    public boolean likeLoadAll;

    public MutableLiveData<List<GoodsInfo>> getGoodsList() {
        return goodsList;
    }

    @Override
    public void onStart() {

        Observable.create((ObservableOnSubscribe<AccountInfo>) infoObs -> {
            AccountInfo info = SpUtil.getInstance().readBean(AccountInfo.class);
            if (info != null) {
                infoObs.onNext(info);
            } else {
                infoObs.onError(new Exception("noInfo"));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new RxObserver<AccountInfo>() {
                    @Override
                    public void onNext(AccountInfo accountInfo) {
                        MainViewModel.this.onLogin(accountInfo);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Router.getInstance().open(AppService.instance.getCurActivity(), "login");
                    }
                });
        store = new RxStore(this);
        store.register(ActionType.LOGIN, ActionType.LOGOUT);
    }

    @Override
    public void onDestory() {
//        SpUtil.getInstance().writeString("current_goods_id", curId);
//        SpUtil.getInstance().writeInt("goods_left", itemLeft);
        store.unRegister();
        super.onDestory();
    }

    @Override
    public void onChange(Action action) {
        if (ActionType.LOGIN.equals(action.getType())) {
            Observable.create((ObservableOnSubscribe<AccountInfo>) infoObs -> {
                AccountInfo info = action.get("account");
                SpUtil.getInstance().saveBean(info);
                infoObs.onNext(info);
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(info -> {
                onLogin(info);
            });
        } else if (ActionType.LOGOUT.equals(action.getType())) {
            accountInfoMutable.postValue(new AccountInfo());
        }
    }

    private void onLogin(AccountInfo info) {
        AppService.instance.setToken(info.token);
        SpUtil.getInstance().writeString("token", info.token);
        AppService.instance.setUid(info.uid);
        accountInfoMutable.postValue(info);
    }

    @Override
    public void onError(Action action) {

    }

    public MutableLiveData<AccountInfo> getAccountInfoMutable() {
        return accountInfoMutable;
    }

    //    //防止重复加载
//    private String requestId;
    private JsonObject lastObj;
    public boolean goodsListEnd;//加载到末尾标志

    public void loadGoodsList() {
        setLoadingStatus(LOADINGMORE);

        RequestBody requestBody;
        if (lastObj != null) {
            requestBody = RequestBody.create(MediaType.parse("application/json"), lastObj.toString());
        } else {
            requestBody = RequestBody.create(MediaType.parse("application/json"), "");
        }
        repository.getGoods(requestBody).doOnSubscribe(this::addDispose).compose(RxTransformer.trans())
                .subscribe(new RxObserver<GoodsData>() {
                    @Override
                    public void onNext(GoodsData goodsData) {
                        if (goodsData.list.size() == 0) {
                            goodsListEnd = true;
                        } else {
                            goodsListEnd = false;
                        }
                        lastObj = goodsData.cursor;
                        setLoadingStatus(LOADMORE_SUCCESS);
                        List<GoodsInfo> temp = goodsList.getValue();
                        if (goodsData.list.size() >= 0) {
                            Collections.reverse(goodsData.list);
                            if (temp == null) {
                                temp = new ArrayList<>();
                            }
                            temp.addAll(0, goodsData.list);
                        }
                        goodsList.setValue(temp);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
//                        requestId = null;
//                        goodsList.setValue(new ArrayList<>());//触发UI刷新
                        lastObj = null;
                        setLoadingStatus(LOADMORE_FAILED);
                    }
                });
    }

     private Long timeStamp;
     private Long lastTimeStamp;

    public void loadLikeList(final boolean refresh) {
        pullToRefreshLike = refresh;
        likeLoadingStatus.setValue(refresh ? REFRESHING : LOADINGMORE);
        if (refresh) {
            timeStamp = null;
        }
        if (lastTimeStamp == null || !lastTimeStamp.equals(timeStamp)) {
            lastTimeStamp = timeStamp;
        } else {
            return;
        }
        repository.getLikeList(timeStamp).doOnSubscribe(this::addDispose).compose(RxTransformer.trans())
                .subscribe(new RxObserver<GoodsData>() {
                    @Override
                    public void onNext(GoodsData data) {
                        List<GoodsInfo> goodsInfos = data.list;
                        if (goodsInfos != null && goodsInfos.size() > 0) {
                            timeStamp = data.ts;
                        }
                        likeLoadAll = false;
                        if (!goodsInfos.isEmpty()) {
                            if (goodsInfos.size() < MainRepository.PAGE_SIZE) {
                                likeLoadAll = true;
                            }
                            if (refresh) {
                                likeList.setValue(goodsInfos);
                            } else {
                                List<GoodsInfo> temp = likeList.getValue();
                                if (temp == null) {
                                    temp = new ArrayList<>();
                                }
                                temp.addAll(goodsInfos);
                                likeList.setValue(temp);
                            }
                        } else {
                            likeLoadAll = true;
                            likeList.setValue(goodsInfos);
                        }
                        likeLoadingStatus.setValue(refresh ? REFRESH_SUCCESS : LOADMORE_SUCCESS);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        likeLoadingStatus.setValue(refresh ? REFRESH_FAILED : LOADMORE_FAILED);
                    }
                });
    }

    public void loadPrizeList() {
        repository.getPrizeList().doOnSubscribe(this::addDispose).compose(RxTransformer.trans())
                .subscribe(new RxObserver<List<GoodsInfo>>() {
                    @Override
                    public void onNext(List<GoodsInfo> goodsInfos) {
                        prizeList.setValue(goodsInfos);
                    }
                });

    }

    public void notifyWin(String goodsId) {
        repository.notifyWin(goodsId).doOnSubscribe(this::addDispose).compose(RxTransformer.trans())
                .subscribe(new RxObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        loadLikeList(true);//通知服务器后刷新心愿单列表
                    }
                });
    }

    public void like(String goodsId, boolean isLike) {
        if (AppService.instance.noNetworkToast()) return;
        String like = isLike ? "LIKE" : "DISLIKE";
        repository.like(goodsId, like).doOnSubscribe(this::addDispose).compose(RxTransformer.trans())
                .subscribe(new RxObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {

                    }

                    @Override
                    public void onError(Throwable t) {
                        AppService.instance.showNetError();
                    }
                });
    }

    public void removeLike(String goodsId) {
        repository.removeLike(goodsId).doOnSubscribe(this::addDispose).compose(RxTransformer.trans()).subscribe(new RxObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {

            }
        });
    }

    public void hasPrize() {
        repository.hasPrize().doOnSubscribe(this::addDispose).compose(RxTransformer.trans()).subscribe(new RxObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                hasPrize.setValue(aBoolean);
            }
        });
    }

    public void hasContactInfo() {
        repository.hasContactInfo().doOnSubscribe(this::addDispose).compose(RxTransformer.trans()).subscribe(new RxObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                hasContactInfo.setValue(aBoolean);
            }
        });
    }

    public MutableLiveData<Integer> getLikeLoadingStatus() {
        return likeLoadingStatus;
    }


    public MutableLiveData<List<GoodsInfo>> getLikeListData() {
        return likeList;
    }

    public MutableLiveData<Boolean> getHasPrize() {
        return hasPrize;
    }

    public MutableLiveData<List<GoodsInfo>> getPrizeList() {
        return prizeList;
    }

    public MutableLiveData<Boolean> getHasContactInfo() {
        return hasContactInfo;
    }
}
