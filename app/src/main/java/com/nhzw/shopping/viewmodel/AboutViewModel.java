package com.nhzw.shopping.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.nhzw.base.BaseViewModel;
import com.nhzw.rx.RxObserver;
import com.nhzw.rx.RxTransformer;
import com.nhzw.shopping.model.entity.UpdateInfo;
import com.nhzw.shopping.model.repository.AboutRepository;

/**
 * Created by ww on 2017/11/3.
 */

public class AboutViewModel extends BaseViewModel<AboutRepository> {

    public MutableLiveData<UpdateInfo> updateStatus = new MutableLiveData<>();
    public void checkUpdate() {
        repository.checkUpdate().doOnSubscribe(this::addDispose).compose(RxTransformer.trans()).subscribe(new RxObserver<UpdateInfo>() {
            @Override
            public void onNext(UpdateInfo updateInfo) {
                updateStatus.setValue(updateInfo);
            }
        });
    }

    @Override
    public void onStart() {

    }
}
