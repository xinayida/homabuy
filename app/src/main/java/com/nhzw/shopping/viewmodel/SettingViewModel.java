package com.nhzw.shopping.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.nhzw.base.BaseViewModel;
import com.nhzw.rx.RxUtils;
import com.nhzw.service.AppService;
import com.nhzw.shopping.model.repository.SettingRepository;
import com.nhzw.utils.ImageLoader;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * =================================
 * 描述: 设置页面viewModel.<br/><br/>
 * 作者：由姚宇编辑于2017/10/31.<br/><br/>
 * =================================
 */
public class SettingViewModel extends BaseViewModel<SettingRepository> {
    private MutableLiveData<Boolean> login = new MutableLiveData<>();
    public MutableLiveData<String> cacheSize = new MutableLiveData<>();

    @Override
    public void onStart() {
        RxUtils.runOnUI(() -> login.setValue(AppService.instance.isLogined()));
//        Observable.create((ObservableOnSubscribe<Boolean>) infoObs -> {
//            infoObs.onNext(AppService.instance.isLogined());
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(info -> {
//            if (info) {
//                login.postValue(true);
//            } else {
//                login.postValue(false);
//            }
//        });
        caculateCacheSize();
    }

    private void caculateCacheSize() {
        Observable.create((ObservableOnSubscribe<String>) sizeObs -> {
            sizeObs.onNext(ImageLoader.getCacheSize());
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(size -> {
            cacheSize.setValue(size);
        });
    }

    public void logout() {
        AppService.instance.asyncHttp(AppService.DOMAIN_LOGIN + "login", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AppService.instance.showToast("登出失败:" + e, false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                login.postValue(false);
            }
        });
    }

    public void clearCache() {
        ImageLoader.clearCache(appComponent.getApp()).subscribe(() -> {
            caculateCacheSize();
        });
    }

    public MutableLiveData<Boolean> getLogin() {
        return login;
    }
}