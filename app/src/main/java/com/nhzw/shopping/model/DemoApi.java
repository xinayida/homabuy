//package com.nhzw.shopping.model;
//
//import com.nhzw.shopping.model.entity.DemoUser;
//
//import java.util.List;
//
//import io.reactivex.Observable;
//import retrofit2.http.GET;
//import retrofit2.http.Headers;
//import retrofit2.http.Query;
//
///**
// * Created by ww on 2017/9/15.
// */
//
//public interface DemoApi {
//
////    @GET(GankApi.DATE_HISTORY)
////    Observable<DateData> getDateHistory();
////
////    @GET("data/{category}/{pageCount}/{page}")
////    Observable<GankData> getGank(@Path("category") String category, @Path("pageCount") int pageCount, @Path("page") int page);
//
//    String HEADER_API_VERSION = "Accept: application/vnd.github.v3+json";
//
//    @Headers({HEADER_API_VERSION})
//    @GET("/users")
//    Observable<List<DemoUser>> getUsers(@Query("since") int lastIdQueried, @Query("per_page") int perPage);
//}
