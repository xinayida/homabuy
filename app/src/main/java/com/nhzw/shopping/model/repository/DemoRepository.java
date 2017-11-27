//package com.nhzw.shopping.model;
//
//import com.nhzw.base.BaseRepository;
//import com.nhzw.base.IRepositoryFactory;
//import com.nhzw.shopping.model.entity.DemoUser;
//
//import java.util.List;
//
//import io.reactivex.Observable;
//import io.rx_cache2.DynamicKey;
//import io.rx_cache2.EvictDynamicKey;
//
///**
// * Created by ww on 2017/9/15.
// */
//
//public class DemoRepository extends BaseRepository {
//
//    public static final int USERS_PER_PAGE = 10;
//
//    public DemoRepository(IRepositoryFactory factory) {
//        super(factory);
//    }
//
//    public Observable<List<DemoUser>> getUser(int lastId, boolean pullToRefresh) {
//        return Observable.just(factory.createRetrofitService(DemoApi.class).getUsers(lastId, USERS_PER_PAGE))
//                .flatMap(listObservable -> {
//                    return factory.createCacheService(DemoCache.class)
//                            .getUsers(listObservable, new DynamicKey(lastId), new EvictDynamicKey(pullToRefresh))
//                            .map((listReply) -> listReply.getData());
//                });
//    }
//
//    @Override
//    public void onDestory() {
//
//    }
//}
