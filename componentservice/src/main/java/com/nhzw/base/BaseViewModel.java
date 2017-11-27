package com.nhzw.base;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.IntDef;

import com.nhzw.component.AppComponent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by ww on 2017/9/14.
 */

public abstract class BaseViewModel<M extends IModel> extends ViewModel implements AppComponent.Injectable, IPresenter {
    /*应用主component*/
    protected AppComponent appComponent;
    protected M repository;
    protected CompositeDisposable compositeDisposable;
    private MutableLiveData<Integer> loadingStatus = new MutableLiveData<>();

    public BaseViewModel() {
        super();
        onStart();
    }

    @Override
    public void inject(AppComponent component) {
        appComponent = component;
        repository = initRepository();
    }



    /**
     * 订阅
     * @param disposable
     */
    public void addDispose(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);//将所有disposable放入,集中处理
    }

    /**
     * 取消订阅
     */
    public void unDispose() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();//保证activity结束时取消所有正在执行的订阅
        }
    }

    @Override
    public void onDestory() {
        unDispose();
        if (repository != null) {
            repository.onDestory();
            repository = null;
        }
    }

    public M initRepository() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<M> entityClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return appComponent.repositoryManager().createRepository(entityClass);
        } else {
            return null;
        }
    }

    public static final int REFRESHING = 1;
    public static final int REFRESH_SUCCESS = 2;
    public static final int REFRESH_FAILED = 3;
    public static final int LOADINGMORE = 4;
    public static final int LOADMORE_SUCCESS = 5;
    public static final int LOADMORE_FAILED = 6;

    @IntDef({REFRESHING, REFRESH_SUCCESS, REFRESH_FAILED, LOADINGMORE, LOADMORE_SUCCESS, LOADMORE_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadingStatus {

    }

    public void setLoadingStatus(@LoadingStatus int status) {
        loadingStatus.setValue(status);
    }

    public MutableLiveData<Integer> getLoadingStatus() {
        return loadingStatus;
    }
}
