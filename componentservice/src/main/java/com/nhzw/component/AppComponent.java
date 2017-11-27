package com.nhzw.component;

import android.content.Context;

import com.nhzw.base.IRepositoryFactory;
import com.nhzw.module.AppModule;
import com.nhzw.module.ClientModule;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by ww on 2017/9/12.
 */

@Singleton
@Component(modules = {AppModule.class, ClientModule.class})
public interface AppComponent {
    Context getApp();

    interface Injectable {
        void inject(AppComponent component);
    }

    //用于管理所有仓库,网络请求层,以及数据缓存层
    IRepositoryFactory repositoryManager();

    OkHttpClient getOkHttpClient();
}
