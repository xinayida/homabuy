//package com.nhzw.shopping.viewmodel;
//
//import android.arch.lifecycle.MutableLiveData;
//
//import com.nhzw.base.BaseViewModel;
//import com.nhzw.rx.RxObserver;
//import com.nhzw.shopping.model.DemoRepository;
//import com.nhzw.shopping.model.entity.DemoUser;
//
//import java.util.List;
//
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * Created by ww on 2017/9/18.
// */
//
//public class DemoViewModel extends BaseViewModel<DemoRepository> {
//    public boolean pullToRefresh;
//    private MutableLiveData<List<DemoUser>> userList = new MutableLiveData<>();
//
//    public MutableLiveData<List<DemoUser>> getUserList() {
//        return userList;
//    }
//
//    public void requestUser(final boolean pullToRefresh, final int lastId) {
//        this.pullToRefresh = pullToRefresh;
//        repository.getUser(lastId, pullToRefresh).subscribeOn(Schedulers.io()).doOnSubscribe(disposable -> {
//            addDispose(disposable);
//            setLoadingStatus(pullToRefresh ? REFRESHING : LOADINGMORE);
//        }).subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new RxObserver<List<DemoUser>>() {
//                    @Override
//                    public void onNext(List<DemoUser> users) {
//                        if (pullToRefresh) {
//                            userList.setValue(users);
//                        } else {
//                            List<DemoUser> temp = userList.getValue();
//                            temp.addAll(users);
//                            userList.setValue(temp);
//                        }
//                        setLoadingStatus(pullToRefresh ? REFRESH_SUCCESS : LOADMORE_SUCCESS);
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        super.onError(t);
//                        setLoadingStatus(pullToRefresh ? REFRESH_FAILED : LOADMORE_FAILED);
//                    }
//                });
//
//    }
//
//    @Override
//    public void onStart() {
//
//    }
//
//}
