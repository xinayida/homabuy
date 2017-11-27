package com.nhzw.shopping.model.repository;

import com.nhzw.base.BaseRepository;
import com.nhzw.base.IRepositoryFactory;
import com.nhzw.service.AppService;
import com.nhzw.shopping.model.API;
import com.nhzw.shopping.model.UpdateResult;

import io.reactivex.Observable;

/**
 * Created by ww on 2017/11/3.
 */

public class AboutRepository extends BaseRepository {
    public AboutRepository(IRepositoryFactory repositoryFactory) {
        super(repositoryFactory);
    }

    public Observable<UpdateResult> checkUpdate() {
        return factory.createRetrofitService(API.class, AppService.DOMAIN_UPDATE).checkUpdate();
    }

    @Override
    public void onDestory() {

    }
}
