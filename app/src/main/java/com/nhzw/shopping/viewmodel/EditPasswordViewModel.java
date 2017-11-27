package com.nhzw.shopping.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.nhzw.base.BaseViewModel;
import com.nhzw.service.AppService;
import com.nhzw.shopping.model.repository.SettingRepository;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * =================================
 * 描述: 设置页面viewModel.<br/><br/>
 * 作者：由姚宇编辑于2017/10/31.<br/><br/>
 * =================================
 */
public class EditPasswordViewModel extends BaseViewModel<SettingRepository> {
    private MutableLiveData<Boolean> login = new MutableLiveData<>();

    @Override
    public void onStart() {
        Observable.create((ObservableOnSubscribe<Boolean>) infoObs -> {
            infoObs.onNext(AppService.instance.isLogined());
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(info -> {
            if (info) {
                login.postValue(true);
            } else {
                login.postValue(false);
            }
        });
    }

    public MutableLiveData<Boolean> getLogin() {
        return login;
    }
}
