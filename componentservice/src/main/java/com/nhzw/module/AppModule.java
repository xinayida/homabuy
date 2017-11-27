package com.nhzw.module;

import android.app.Application;
import android.content.Context;

import com.nhzw.base.IRepositoryFactory;
import com.nhzw.base.RepositoryFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ww on 2017/9/12.
 */

@Module
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    public Context getApp() {
        return application;
    }

    @Provides
    @Singleton
    IRepositoryFactory provideRepository(RepositoryFactory repositoryFactory) {
        return repositoryFactory;
    }
}
