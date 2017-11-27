package com.nhzw.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.nhzw.component.AppComponent;

/**
 * Created by ww on 2017/9/14.
 */

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private AppComponent appContext;

    public ViewModelFactory(AppComponent appContext) {
        this.appContext = appContext;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        T t = super.create(modelClass);
        if (t instanceof AppComponent.Injectable) {
            ((AppComponent.Injectable) t).inject(appContext);
        }
        return t;
    }
}