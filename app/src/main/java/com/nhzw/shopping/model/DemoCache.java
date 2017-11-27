//package com.nhzw.shopping.model;
//
//import com.nhzw.shopping.model.entity.DemoUser;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.rx_cache2.DynamicKey;
//import io.rx_cache2.EvictProvider;
//import io.rx_cache2.LifeCache;
//import io.rx_cache2.Reply;
//
///**
// * Created by ww on 2017/9/19.
// */
//
//public interface DemoCache {
//    @LifeCache(duration = 2, timeUnit = TimeUnit.MINUTES)
//    Observable<Reply<List<DemoUser>>> getUsers(Observable<List<DemoUser>> users, DynamicKey idLastUserQueried, EvictProvider evictProvider);
//}
