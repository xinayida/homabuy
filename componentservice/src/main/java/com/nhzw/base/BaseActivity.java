package com.nhzw.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.nhzw.service.AppService;
import com.nhzw.widget.LoadingDialog;
import com.umeng.analytics.MobclickAgent;
import com.xinayida.lib.annotation.AFInject;
import com.xinayida.lib.utils.SlidrUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Activity基类
 * Created by ww on 2017/9/14.
 */

public abstract class BaseActivity<V extends BaseViewModel> extends AppCompatActivity implements IView {

    protected V viewModel;
    protected Unbinder unbinder;
    private LoadingDialog loadingDialog;
    protected Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPreCreate();
        if (savedInstanceState != null) {
            String token = savedInstanceState.getString("token");
            int uid = savedInstanceState.getInt("uid");
            if (token != null) {
                AppService.instance.setToken(token);
                AppService.instance.setUid(uid);
            }
        }
        context = this;
        if (getClass().isAnnotationPresent(AFInject.class)) {
            AFInject annotation = getClass().getAnnotation(AFInject.class);
            boolean enableSlider = annotation.enableSlidr();// 是否开启侧滑关闭
            int backId = annotation.btnBackId();//后退按钮ID
            int contentId = annotation.contentViewId();
            if (contentId != -1) {
                setContentView(contentId);
                unbinder = ButterKnife.bind(this);
            }
            if (backId != -1) {
                findViewById(backId).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBack();
                    }
                });
            }
            if (enableSlider) {
                SlidrUtil.initSlidrDefaultConfig(this, true);
            }
        }
        viewModel = createViewModel();
        setup(savedInstanceState);
    }

    protected void onPreCreate(){
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("token", AppService.instance.getToken());
        outState.putInt("uid", AppService.instance.getUid());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null && unbinder != Unbinder.EMPTY) unbinder.unbind();
        unbinder = null;
        if (viewModel != null) {
            viewModel.onDestory();
            viewModel = null;
        }
    }

    @Override
    public V createViewModel() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<V> entityClass = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return ViewModelProviders.of(this, new ViewModelFactory(AppService.instance.getAppComponent())).get(entityClass);
        } else {
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {//屏蔽菜单按钮
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void onBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    public void replaceFragment(int containerViewId, Fragment fragment, String tag) {
        if (null == getSupportFragmentManager().findFragmentByTag(tag)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(containerViewId, fragment, tag)
                    .commit();
        }
    }

    protected abstract void setup(Bundle savedInstanceState);

    protected void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.build();
        } else if (loadingDialog.isVisible()) {
            return;
        }
        loadingDialog.show(getSupportFragmentManager());
    }

    protected void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismissAllowingStateLoss();
        }
    }

}
